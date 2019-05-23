package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.consts.BooleanType;
import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.common.model.consts.TimeUnit;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.common.utils.ParamExpressionUtils;
import cn.lite.flow.console.common.utils.QuartzUtils;
import cn.lite.flow.console.common.utils.TaskVersionUtils;
import cn.lite.flow.console.dao.mapper.TaskInstanceMapper;
import cn.lite.flow.console.dao.mapper.TaskVersionMapper;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskInstanceDependency;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.consts.TaskStatus;
import cn.lite.flow.console.model.consts.TaskVersionFinalStatus;
import cn.lite.flow.console.model.consts.TaskVersionStatus;
import cn.lite.flow.console.model.query.TaskVersionQM;
import cn.lite.flow.console.service.TaskDependencyService;
import cn.lite.flow.console.service.TaskInstanceDependencyService;
import cn.lite.flow.console.service.TaskInstanceService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionService;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class TaskVersionServiceImpl implements TaskVersionService {

    private final static Logger LOG = LoggerFactory.getLogger(TaskVersionServiceImpl.class);

    @Autowired
    private TaskVersionMapper taskVersionMapper;

    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDependencyService taskDependencyService;

    @Autowired
    private TaskInstanceDependencyService instanceDependencyService;

    @Override
    public void add(TaskVersion model) {
        taskVersionMapper.insert(model);
    }

    @Override
    public TaskVersion getById(long id) {
        return taskVersionMapper.getById(id);
    }

    @Override
    public int update(TaskVersion model) {
       return taskVersionMapper.update(model);
    }

    @Override
    public int count(TaskVersionQM queryModel) {
        return taskVersionMapper.count(queryModel);
    }

    @Override
    public List<TaskVersion> list(TaskVersionQM queryModel) {
        return taskVersionMapper.findList(queryModel);
    }

    @Override
    public int updateWithStatus(TaskVersion taskVersion, int status) {
        return taskVersionMapper.updateWithStatus(taskVersion, status);
    }

    @Override
    public TaskVersion getTaskVersion(long taskId, long taskVersion) {

        return taskVersionMapper.getTaskVersion(taskId, taskVersion);
    }

    @Override
    public TaskInstance getLatestInstance(long taskVersionId) {

        return taskInstanceMapper.getVersionLatestInstance(taskVersionId);
    }

    @Override
    @Transactional("consoleTxManager")
    public boolean kill(long taskVersionId) {
        return this.kill(taskVersionId, true);
    }

    @Override
    @Transactional("consoleTxManager")
    public boolean kill(long taskVersionId, boolean isExecutorCallback) {
        TaskInstance latestInstance = this.getLatestInstance(taskVersionId);
        taskInstanceService.kill(latestInstance.getId(), isExecutorCallback);
        TaskVersion updateTaskVersion = new TaskVersion();
        updateTaskVersion.setId(taskVersionId);
        updateTaskVersion.setStatus(TaskVersionStatus.KILLED.getValue());
        updateTaskVersion.setFinalStatus(TaskVersionFinalStatus.KILLED.getValue());
        this.update(updateTaskVersion);

        return true;
    }

    @Override
    @Transactional("consoleTxManager")
    public boolean fix(long taskVersionId) {
        TaskVersion taskVersion = taskVersionMapper.getById(taskVersionId);
        /**
         * 还在运行中的直接kill掉
         */
        if(taskVersion.getFinalStatus() == TaskVersionFinalStatus.NEW.getValue()){
            this.kill(taskVersionId);

        }
        Task task = taskService.getById(taskVersion.getTaskId());
        Long pluginId = task.getPluginId();

        /**
         * 设置初始状态
         */
        TaskVersion updateTaskVersion = new TaskVersion();
        updateTaskVersion.setId(taskVersionId);
        updateTaskVersion.setStatus(TaskVersionStatus.INIT.getValue());
        updateTaskVersion.setFinalStatus(TaskVersionFinalStatus.NEW.getValue());
        this.update(updateTaskVersion);

        /**
         * 新建实例
         */
        TaskInstance latestInstance = this.getLatestInstance(taskVersionId);
        Date logicRunTime = latestInstance.getLogicRunTime();
        /**
         * 处理插件配置中的时间参数
         */
        String pluginConfig = ParamExpressionUtils.handleTimeExpression(task.getPluginConf(), String.valueOf(taskVersion.getVersionNo()));
        TaskInstance taskInstance = this.addVersionInstance(taskVersionId, pluginId, pluginConfig, logicRunTime);
        this.addInstanceDependencies(taskInstance.getId());

        return true;
    }

    @Override
    @Transactional("consoleTxManager")
    public Tuple<Boolean, String> retry(long taskVersionId) {

        TaskVersion taskVersion = taskVersionMapper.getById(taskVersionId);
        Task task = taskService.getById(taskVersion.getTaskId());
        /**
         * 没有配置重试，直接返回
         */
        if(task.getIsRetry() == null || task.getIsRetry() != BooleanType.TRUE.getValue()){
            return Tuple.of(false, "no retry config");
        }
        if(taskVersion.getFinalStatus() == TaskVersionFinalStatus.NEW.getValue()){
            return Tuple.of(false, "task version can not retry, versionId:" + taskVersionId);
        }
        Long pluginId = task.getPluginId();

        String retryConf = task.getRetryConf();
        if(StringUtils.isBlank(retryConf)){
            return Tuple.of(false, "no retry config detail");
        }
        JSONObject retryConfigObj = JSONObject.parseObject(retryConf);
        Integer retryNum = retryConfigObj.getInteger(Constants.RETRY_NUM);
        Integer retryPeriod = retryConfigObj.getInteger(Constants.RETRY_PERIOD);

        Integer versionRetryNum = taskVersion.getRetryNum();
        if(versionRetryNum == null){
            versionRetryNum = 0;
        }
        /**
         * 已经超过重试次数就不再重试
         */
        if(versionRetryNum >= retryNum){
            return Tuple.of(false, "retryNum is greater than:" + versionRetryNum);
        }

        versionRetryNum += 1;

        LOG.info("task version retry, taskName:{}, versionId:{}, retryNum:{}", task.getName(), taskVersion.getId(), retryNum);
        /**
         * 设置初始状态
         */
        TaskVersion updateTaskVersion = new TaskVersion();
        updateTaskVersion.setId(taskVersionId);
        updateTaskVersion.setRetryNum(versionRetryNum);
        updateTaskVersion.setStatus(TaskVersionStatus.INIT.getValue());
        updateTaskVersion.setFinalStatus(TaskVersionFinalStatus.NEW.getValue());
        this.update(updateTaskVersion);

        /**
         * 新建实例
         */
        TaskInstance latestInstance = this.getLatestInstance(taskVersionId);
        Date logicRunTime = latestInstance.getLogicRunTime();

        /**
         * 如果逻辑开始时间在当前时间之前，逻辑运行时间是当前时间
         * 如果在当前时间之后，说明还没有运行到
         */
        Date now = DateUtils.getNow();
        Date retryLogicRunTime = DateUtils.addMinute(now, retryPeriod);
        if(logicRunTime.before(retryLogicRunTime)){
            logicRunTime = retryLogicRunTime;
        }

        /**
         * 处理插件配置中的时间参数
         */
        String pluginConfig = ParamExpressionUtils.handleTimeExpression(task.getPluginConf(), String.valueOf(taskVersion.getVersionNo()));
        TaskInstance taskInstance = this.addVersionInstance(taskVersionId, pluginId, pluginConfig, logicRunTime);
        this.addInstanceDependencies(taskInstance.getId());

        return Tuple.of(true, "");

    }

    @Override
    public boolean deepFix(long taskVersionId) {
        this.deepFixVersionById(taskVersionId, Sets.newHashSet());
        return false;
    }
    /**
     * 递归修复任务版本以及其下游
     * @param id
     */
    private void deepFixVersionById(long id, Set<Long> fixedIdSet){
        TaskVersion taskVersion = this.getById(id);
        LOG.info("deep fix version, taskId:{} versionId:{} versionNo:{}", taskVersion.getTaskId(), taskVersion.getId(), taskVersion.getVersionNo());
        if(fixedIdSet.contains(id)){
            LOG.info("deep fix version has bean fixed, taskId:{} versionId:{} versionNo:{}", taskVersion.getTaskId(), taskVersion.getId(), taskVersion.getVersionNo());
            return;
        }
        try {
            this.fix(id);
        }catch (Throwable e){
            LOG.error("deep fix task verison error,versionId:" + id, e);
        }
        fixedIdSet.add(id);
        List<Long> versionIds = this.getDownstreamVersionIds(id);
        if(CollectionUtils.isNotEmpty(versionIds)){
            for(Long versionId : versionIds){
                deepFixVersionById(versionId, fixedIdSet);
            }
        }

    }

    @Override
    @Transactional("consoleTxManager")
    public boolean ignore(long taskVersionId) {
        /**
         * kill executor任务，不用回调
         */
        this.kill(taskVersionId, false);

        //任务版本置为成功状态
        TaskVersion taskVersionUpdate = new TaskVersion();
        taskVersionUpdate.setId(taskVersionId);
        taskVersionUpdate.setStatus(TaskVersionStatus.SUCCESS.getValue());
        taskVersionUpdate.setFinalStatus(TaskVersionFinalStatus.SUCCESS.getValue());
        this.update(taskVersionUpdate);

        //任务实例置为成功状态
        TaskInstance instance = getLatestInstance(taskVersionId);
        TaskInstance instanceUpdate = new TaskInstance();
        instanceUpdate.setId(instance.getId());
        instanceUpdate.setMsg("手动忽略");
        instanceUpdate.setStatus(TaskVersionStatus.SUCCESS.getValue());

        taskInstanceService.update(instanceUpdate);
        return true;
    }

    @Override
    public TaskInstance addVersionInstance(long taskVersionId, long pluginId, String pluginConfig, Date logicRunTime) {
        TaskVersion taskVersion = taskVersionMapper.getById(taskVersionId);
        TaskInstance taskInstance = TaskInstance.newInstanceByVersion(taskVersion);
        taskInstance.setStatus(TaskVersionStatus.INIT.getValue());
        taskInstance.setLogicRunTime(logicRunTime);
        taskInstance.setPluginId(pluginId);
        taskInstance.setPluginConf(pluginConfig);
        taskInstanceService.add(taskInstance);
        return taskInstance;
    }

    @Override
    @Transactional("consoleTxManager")
    public void addInstanceDependencies(long instanceId) {
        TaskInstance taskInstance = taskInstanceService.getById(instanceId);
        LOG.info("add instance dependency, instance:{}", instanceId);
        Long taskId = taskInstance.getTaskId();
        Long versionNo = taskInstance.getTaskVersionNo();
        Task task = taskService.getById(taskId);
        //获取上游
        List<TaskDependency> upstreams = taskDependencyService.getUpstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(upstreams)){
            List<TaskInstanceDependency> instanceDependencies = Lists.newArrayList();
            upstreams.forEach(dependency -> {
                Long upstreamTaskId = dependency.getUpstreamTaskId();
                /**
                 * 忽略无效的依赖
                 */
                if(dependency.getStatus() != StatusType.OFF.getValue()){
                    Task upstreamTask = taskService.getById(upstreamTaskId);
                    if(upstreamTask.getStatus() == TaskStatus.ONLINE.getValue()){
                            //计算实例与上游任务版本的依赖
                        List<Long> upstreamTaskVersions = TaskVersionUtils.getUpstreamTaskVersions(task, versionNo, upstreamTask, dependency);
                        if(CollectionUtils.isNotEmpty(upstreamTaskVersions)){
                            upstreamTaskVersions.forEach(upstreamTaskVersion -> {
                                TaskInstanceDependency instanceDependency = TaskInstanceDependency.newInstance(instanceId, upstreamTaskId, upstreamTaskVersion);
                                instanceDependency.setStatus(StatusType.ON.getValue());
                                LOG.info("add instance dependency,info:{}", instanceDependency);
                                instanceDependencies.add(instanceDependency);
                            });
                        }
                    }
                }else {
                    LOG.error("dependency:{} is offline, instance:{} ,msg:{}", dependency.getId(), instanceId, dependency.toString());
                }
            });
            if(CollectionUtils.isNotEmpty(instanceDependencies)){
                instanceDependencyService.addBatch(instanceDependencies);
            }
        }

    }

    @Override
    public List<TaskVersion> getValidVersion(long taskId) {
        TaskVersionQM qm = new TaskVersionQM();
        qm.setTaskId(taskId);
        qm.setFinalStatus(TaskVersionFinalStatus.NEW.getValue());
        List<TaskVersion> taskVersions = taskVersionMapper.findList(qm);
        return taskVersions;
    }

    @Override
    public List<TaskVersion> getByIds(List<Long> versionIds) {
        if(CollectionUtils.isEmpty(versionIds)){
            return null;
        }
        TaskVersionQM qm = new TaskVersionQM();
        qm.setIds(versionIds);
        return taskVersionMapper.findList(qm);
    }

    @Override
    public List<Long> getDownstreamVersionIds(long taskVersionId) {
        TaskVersion taskVersion = taskVersionMapper.getById(taskVersionId);
        List<TaskInstanceDependency> instanceDependencies = instanceDependencyService.listVersionValidDependency(taskVersion.getTaskId(), taskVersion.getVersionNo());
        if(CollectionUtils.isNotEmpty(instanceDependencies)){
            List<Long> instanceIds = instanceDependencies.stream().map(TaskInstanceDependency::getInstanceId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(instanceIds)){
                List<TaskInstance> instances = taskInstanceService.getByIds(instanceIds);
                if(CollectionUtils.isNotEmpty(instances)){
                    Set<Long> versionIds = instances.stream().map(TaskInstance::getTaskVersionId).collect(Collectors.toSet());
                    return Lists.newArrayList(versionIds);
                }
            }
        }
        return null;
    }

    @Override
    @Transactional("consoleTxManager")
    public void calVersionAndInstanceWithDependency(long taskId, Date startTime, Date endTime) {
        Task task = taskService.getById(taskId);
        Long pluginId = task.getPluginId();

        TimeUnit timeUnit = TimeUnit.getType(task.getPeriod());
        LOG.info("calculate task version, taskId:{}, startTime:{}, endTime:{}", taskId, DateUtils.formatToDateTimeStr(startTime), DateUtils.formatToDateTimeStr(endTime));
        //计算时间区间运行时间
        String cronExpression = task.getCronExpression();
        String crontab = QuartzUtils.completeCrontab(cronExpression);
        List<Date> taskRunTimes = QuartzUtils.getRunDateTimes(crontab, startTime, endTime);
        if (CollectionUtils.isEmpty(taskRunTimes)) {
            return;
        }
        taskRunTimes.forEach(runTime -> {
            Long taskVersionNo = TaskVersionUtils.getTaskVersion(runTime, timeUnit);
            TaskVersion taskVersion = this.getTaskVersion(taskId, taskVersionNo);
            if (taskVersion == null) {
                LOG.info("init task version, taskId:{}, versionNo:{}", taskId, taskVersionNo);
                taskVersion = TaskVersion.newInstance(taskId, taskVersionNo);
                this.add(taskVersion);
            } else {
                LOG.info("task version is already exists, taskId:{}, versionNo:{}", taskId, taskVersionNo);
                /**
                 * 如果实例依旧在执行中
                 * 先kill掉，再重新生成新的实例
                 */
                if(taskVersion.getFinalStatus().intValue() == TaskVersionFinalStatus.NEW.getValue()){
                    this.kill(taskVersion.getId());
                }
                taskVersion.setStatus(TaskVersionStatus.INIT.getValue());
                taskVersion.setFinalStatus(TaskVersionFinalStatus.NEW.getValue());
                this.update(taskVersion);
            }
            /**
             * 处理插件配置中的时间参数
             */
            String pluginConfig = ParamExpressionUtils.handleTimeExpression(task.getPluginConf(), String.valueOf(taskVersionNo));

            //创建实例
            TaskInstance taskInstance = this.addVersionInstance(taskVersion.getId(), pluginId, pluginConfig, runTime);
            //创建实例依赖
            this.addInstanceDependencies(taskInstance.getId());

        });
    }

    @Override
    public int getLessThanNoCount(long taskId, int finalStatus, long versionNo) {
        return taskVersionMapper.getLessThanNoCount(taskId, finalStatus, versionNo);
    }
}
