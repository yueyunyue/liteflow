package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.consts.BooleanType;
import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.console.common.consts.TimeUnit;
import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.SourceTypeEnum;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.common.utils.ParamExpressionUtils;
import cn.lite.flow.console.common.utils.TaskVersionUtils;
import cn.lite.flow.console.dao.mapper.TaskMapper;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.basic.UserGroupAuthMid;
import cn.lite.flow.console.model.consts.TaskStatus;
import cn.lite.flow.console.model.query.TaskQM;
import cn.lite.flow.console.service.TaskDependencyService;
import cn.lite.flow.console.service.TaskInstanceService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionDailyInitService;
import cn.lite.flow.console.service.TaskVersionService;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final static Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskVersionService taskVersionService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskDependencyService dependencyService;

    @Autowired
    private TaskVersionDailyInitService dailyInitService;

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;

    @Override
    @Transactional("consoleTxManager")
    public void add(Task model) {
        model.setStatus(TaskStatus.NEW.getValue());
        taskMapper.insert(model);

        /**
         * 创建人拥有所有权限
         */
        UserGroupAuthMid userGroupAuthMid = new UserGroupAuthMid();
        userGroupAuthMid.setSourceId(model.getUserId());
        userGroupAuthMid.setSourceType(SourceTypeEnum.SOURCE_TYPE_USER.getCode());
        userGroupAuthMid.setTargetId(model.getId());
        userGroupAuthMid.setTargetType(AuthCheckTypeEnum.AUTH_CHECK_TASK.getCode());
        userGroupAuthMid.setHasEditAuth(BooleanType.TRUE.getValue());
        userGroupAuthMid.setHasExecuteAuth(BooleanType.TRUE.getValue());
        userGroupAuthMid.setUserId(model.getUserId());
        userGroupAuthMidService.add(userGroupAuthMid);

    }

    @Override
    public Task getById(long id) {
        return taskMapper.getById(id);
    }

    @Override
    public int update(Task model) {
       return taskMapper.update(model);
    }

    @Override
    public int count(TaskQM queryModel) {
        return taskMapper.count(queryModel);
    }

    @Override
    public List<Task> list(TaskQM queryModel) {
        return taskMapper.findList(queryModel);
    }

    @Override
    public List<Task> getByIds(List<Long> taskIds) {
        if(CollectionUtils.isEmpty(taskIds)){
            return null;
        }
        List<Task> tasks = Lists.newArrayList();

        List<List<Long>> partitionIds = Lists.partition(taskIds, CommonConstants.LIST_BATCH_SIZE);
        partitionIds.forEach(ids -> {
            TaskQM qm = new TaskQM();
            qm.setIds(ids);
            List<Task> taskList = taskMapper.findList(qm);
            if(CollectionUtils.isNotEmpty(taskList)){
                tasks.addAll(taskList);
            }
        });
        return tasks;
    }

    @Override
    public Map<Long, Task> getTaskInfo(List<Long> taskIds) {
        List<Task> tasks = this.getByIds(taskIds);
        if(CollectionUtils.isNotEmpty(tasks)){
            Map<Long, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
            return taskMap;
        }
        return null;
    }

    @Override
    @Transactional("consoleTxManager")
    public boolean online(long taskId) {
        Task task = taskMapper.getById(taskId);
        if (task.getStatus() == TaskStatus.ONLINE.getValue()) {
            LOG.error("task is aleady online:" + taskId);
            return true;
        }
        //状态置为上线
        Task updateTask = new Task();
        updateTask.setStatus(TaskStatus.ONLINE.getValue());
        updateTask.setId(taskId);
        update(updateTask);

        /**
         * 将相关依赖置为有效
         */
        List<TaskDependency> dependencies = Lists.newArrayList();
        List<TaskDependency> upstreamDependencies = dependencyService.getUpstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(upstreamDependencies)){
            dependencies.addAll(upstreamDependencies);
        }
        List<TaskDependency> downstreamDependencies = dependencyService.getDownstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(downstreamDependencies)){
            dependencies.addAll(downstreamDependencies);
        }

        if(CollectionUtils.isNotEmpty(dependencies)){
            dependencies.forEach(dependency -> {
                dependencyService.validDependency(dependency.getId());
            });
        }
        /**
         * 计算任务版本以及依赖
         */

        Date startTime = DateUtils.getNow();
        Date endTime = DateUtils.getEndTimeOfDay(startTime);

        taskVersionService.calVersionAndInstanceWithDependency(taskId, startTime, endTime);

        /**
         * dailyinit 恢复
         */
        dailyInitService.enableDailyInit(taskId, DateUtils.getTommorrowLongDate());

        return true;
    }


    @Override
    @Transactional("consoleTxManager")
    public boolean offline(long taskId) {

        Tuple<Boolean, List<TaskDependency>> tryResult = tryOffline(taskId);
        if(!tryResult.getA()){
            return false;
        }


        //状态置为下线
        Task updateTask = new Task();
        updateTask.setStatus(TaskStatus.OFFLINE.getValue());
        updateTask.setId(taskId);
        this.update(updateTask);

        /**
         * kill掉任务正在运行的任务版本，必须要最后处理
         */
        List<TaskVersion> taskVersions = taskVersionService.getValidVersion(taskId);
        if (CollectionUtils.isNotEmpty(taskVersions)) {
            taskVersions.forEach(taskVersion -> {
                taskVersionService.kill(taskVersion.getId());
            });
        }
        /**
         * dailyinit 置为无效
         */
        dailyInitService.disableDailyInit(taskId, DateUtils.getTommorrowLongDate());

        return true;
    }

    @Override
    public Tuple<Boolean, List<TaskDependency>> tryOffline(long taskId) {

        Task task = taskMapper.getById(taskId);
        if (task.getStatus() == TaskStatus.OFFLINE.getValue()) {
            LOG.error("task is aleady offline:" + taskId);
            return new Tuple<>(true, null);
        }
        /**
         * 上游依赖失效
         */
        List<TaskDependency> dependencies = Lists.newArrayList();
        List<TaskDependency> upstreamDependencies = dependencyService.getUpstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(upstreamDependencies)){
            dependencies.addAll(upstreamDependencies);
        }
        /**
         * 下游依赖失效
         */
        List<TaskDependency> downstreamDependencies = dependencyService.getDownstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(downstreamDependencies)){
            dependencies.addAll(downstreamDependencies);
            /**
             * 下游是否有正在运行的任务版本
             */
            for(TaskDependency dependency : downstreamDependencies){
                Long downstreamTaskId = dependency.getTaskId();

                List<TaskVersion> validVersions = taskVersionService.getValidVersion(downstreamTaskId);
                if(CollectionUtils.isNotEmpty(validVersions)){
                    throw new ConsoleRuntimeException("下游任务:id(" + downstreamTaskId + ")仍有任务版本正在运行，不能下线");
                }
            }
        }

        return new Tuple<>(true, dependencies);
    }

    @Override
    public int statisByStatus(Integer status) {
        return Optional.ofNullable(taskMapper.statisByStatus(status)).orElse(0);
    }

    @Override
    @Transactional("consoleTxManager")
    public void run(long taskId) {
        Date now = DateUtils.getNow();
        Task task = taskMapper.getById(taskId);
        if(task.getStatus() == TaskStatus.ONLINE.getValue()){
            List<TaskVersion> validVersions = taskVersionService.getValidVersion(taskId);
            if(CollectionUtils.isNotEmpty(validVersions)){
                throw new ConsoleRuntimeException("当前任务仍有任务版本在运行，不能运行任务");
            }
        }
        /**
         * 以当前时间为基准，生成任务版本
         */
        TimeUnit period = TimeUnit.MINUTE;
        Long runTaskVersionNo = TaskVersionUtils.getTaskVersion(now, period);
        TaskVersion runTaskVersion = TaskVersion.newInstance(taskId, runTaskVersionNo);
        taskVersionService.add(runTaskVersion);
        /**
         * 1.处理插件配置中的时间参数
         * 2.生成实例
         */
        String pluginConfig = ParamExpressionUtils.handleTimeExpression(task.getPluginConf(), String.valueOf(runTaskVersionNo));
        taskVersionService.addVersionInstance(runTaskVersion.getId(), task.getPluginId(), pluginConfig, now);

    }
}
