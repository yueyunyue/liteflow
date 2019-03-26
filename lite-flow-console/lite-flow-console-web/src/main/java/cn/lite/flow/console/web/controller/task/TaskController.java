package cn.lite.flow.console.web.controller.task;

import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.enums.TargetTypeEnum;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.utils.QuartzUtils;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.*;
import cn.lite.flow.console.model.query.TaskQM;
import cn.lite.flow.console.service.FlowDependencySnapshotService;
import cn.lite.flow.console.service.FlowService;
import cn.lite.flow.console.service.TaskDependencyService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import cn.lite.flow.console.web.annotation.AuthCheck;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import cn.lite.flow.executor.client.ExecutorContainerRpcService;
import cn.lite.flow.executor.client.ExecutorPluginRpcService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 任务相关
 * @author: yueyunyue
 * @create: 2018-07-25
 **/
@RestController
@RequestMapping("console/task")
public class TaskController extends BaseController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;

    @Autowired
    private TaskDependencyService taskDependencyService;

    @Autowired
    private FlowDependencySnapshotService snapshotService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private ExecutorPluginRpcService executorPluginRpcService;

    @Autowired
    private ExecutorContainerRpcService executorContainerRpcService;

    /**
     * 获取任务列表
     *
     * @param nameLike      名称模糊查询
     * @param status        状态
     * @param pluginId      插件id
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return
     */
    @RequestMapping(value = "list")
    public String list(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "nameLike", required = false) String nameLike,
            @RequestParam(value = "period", required = false) Integer period,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pluginId", required = false) Long pluginId,
            @RequestParam(value = "flowId", required = false) Long flowId,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {

        SessionUser user = getUser();

        int total = 0;
        JSONArray datas = new JSONArray();

        List<Long> taskIds = null;

        if(flowId != null){
            taskIds = flowService.getTaskIds(flowId);
            if(CollectionUtils.isEmpty(taskIds)){
                return ResponseUtils.listEmpty();
            }
        }else{
            if (!user.getIsSuper()) {
                /**不是超级管理员，则只能查看自己的*/
                taskIds = userGroupAuthMidService.getTargetId(user.getId(), user.getGroupIds(), TargetTypeEnum.TARGET_TYPE_TASK.getCode());
                if (CollectionUtils.isEmpty(taskIds)) {
                    return ResponseUtils.list(total, datas);
                }
            }
        }
        TaskQM taskQM = new TaskQM();
        taskQM.setIds(taskIds);
        taskQM.setId(id);
        taskQM.setNameLike(nameLike);
        taskQM.setStatus(status);
        taskQM.setPluginId(pluginId);
        taskQM.setPeriod(period);
        taskQM.setUserId(userId);
        taskQM.setPage(pageNum, pageSize);
        taskQM.addOrderDesc(TaskQM.COL_ID);

        List<Task> taskList = taskService.list(taskQM);


        if (CollectionUtils.isNotEmpty(taskList)) {

            List<Long> userIds = taskList.stream()
                    .map(Task::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            total = taskService.count(taskQM);
            taskList.forEach(task -> {
                JSONObject obj = ModelUtils.getTaskObj(task);
                Map<Long, String> userInfo = getUserName(userIds);
                setUserInfo(obj, task.getUserId(), userInfo);
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 添加任务
     *
     * @param name                  名称
     * @param cronExpression        调度表达式
     * @param period                调度周期
     * @param version               版本
     * @param isConcurrency         是否可以并发
     * @param executeStrategy       执行策略
     * @param pluginId              插件id
     * @param pluginConf            插件配置
     * @param isRetry               是否重试
     * @param retryConf             重试策略
     * @param description           说明
     * @return
     */
    @RequestMapping(value = "add")
    public String add(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "cronExpression") String cronExpression,
            @RequestParam(value = "period") Integer period,
            @RequestParam(value = "version", required = false, defaultValue = "1") Integer version,
            @RequestParam(value = "concurrency", required = false, defaultValue = "0") Integer isConcurrency,
            @RequestParam(value = "concurrentStrategy", required = false, defaultValue = "1") Integer executeStrategy,
            @RequestParam(value = "pluginId") Long pluginId,
            @RequestParam(value = "pluginConf", required = false) String pluginConf,
            @RequestParam(value = "isRetry") Integer isRetry,
            @RequestParam(value = "retryConf", required = false, defaultValue = "") String retryConf,
            @RequestParam(value = "maxRunTime") Integer maxRunTime,
            @RequestParam(value = "alarmEmail", required = false) String alarmEmail,
            @RequestParam(value = "alarmPhone", required = false) String alarmPhone,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {
        TaskQM taskQM = new TaskQM();
        taskQM.setName(name);
        List<Task> taskList = taskService.list(taskQM);
        if (CollectionUtils.isNotEmpty(taskList)) {
            return ResponseUtils.error("该名称已经存在");
        }

        String cron = QuartzUtils.completeCrontab(cronExpression);
        boolean crontabValid = QuartzUtils.isCrontabValid(cron);
        if (!crontabValid) {
            throw new ConsoleRuntimeException("请确认cron表达式的合法性");
        }

        Task task = new Task();
        task.setName(name);
        task.setCronExpression(cronExpression);
        task.setPeriod(period);
        task.setVersion(version);
        task.setIsConcurrency(isConcurrency);
        task.setExecuteStrategy(executeStrategy);
        task.setPluginId(pluginId);
        task.setPluginConf(pluginConf);
        task.setIsRetry(isRetry);
        task.setRetryConf(retryConf);
        task.setDescription(description);
        task.setMaxRunTime(maxRunTime);
        task.setAlarmEmail(alarmEmail);
        task.setAlarmPhone(alarmPhone);
        task.setUserId(getUser().getId());
        taskService.add(task);
        return ResponseUtils.success("添加任务成功");
    }

    /**
     * 复制任务
     *
     * @param id            任务id
     * @param name          任务名称
     * @param description   任务说明
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK, operateType = OperateTypeEnum.OPERATE_TYPE_EDIT)
    @RequestMapping(value = "copy")
    public String copy(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description
    ) {
        Task task = taskService.getById(id);
        Preconditions.checkArgument(task != null, "未获取到相关任务");
        task.setName(name);
        task.setDescription(description);
        taskService.add(task);
        return ResponseUtils.success("复制成功");
    }


    /**
     * 编辑任务
     *
     * @param id                任务id             如果为空，则是添加  否则是更新
     * @param name              名称
     * @param cronExpression    调度表达式
     * @param period            周期
     * @param isConcurrency     是否可并发执行     1-可以  0-否
     * @param pluginId          插件id
     * @param pluginConf        插件需要的参数
     * @param isRetry           是否可以重试      1-可以  0-否
     * @param retryConf         重试规则          json串
     * @param description       说明
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK, operateType = OperateTypeEnum.OPERATE_TYPE_EDIT)
    @RequestMapping(value = "edit", method = {RequestMethod.POST})
    public String edit(
            @RequestParam(value = "id") long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "cronExpression") String cronExpression,
            @RequestParam(value = "period") Integer period,
            @RequestParam(value = "concurrency") Integer isConcurrency,
            @RequestParam(value = "pluginId") Long pluginId,
            @RequestParam(value = "pluginConf", required = false) String pluginConf,
            @RequestParam(value = "isRetry") Integer isRetry,
            @RequestParam(value = "retryConf", required = false, defaultValue = "") String retryConf,
            @RequestParam(value = "maxRunTime") Integer maxRunTime,
            @RequestParam(value = "alarmEmail", required = false) String alarmEmail,
            @RequestParam(value = "alarmPhone", required = false) String alarmPhone,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {

        String cron = QuartzUtils.completeCrontab(cronExpression);
        boolean crontabValid = QuartzUtils.isCrontabValid(cron);
        if (!crontabValid) {
            throw new ConsoleRuntimeException("请确认表达式的合法性");
        }

        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setCronExpression(cronExpression);
        task.setPeriod(period);
        task.setIsConcurrency(isConcurrency);
        task.setPluginId(pluginId);
        task.setPluginConf(pluginConf);
        task.setIsRetry(isRetry);
        task.setRetryConf(retryConf);
        task.setMaxRunTime(maxRunTime);
        task.setAlarmPhone(alarmPhone);
        task.setAlarmEmail(alarmEmail);
        task.setDescription(description);
        taskService.update(task);
        return ResponseUtils.success("编辑任务成功");
    }

    /**
     * 上线任务
     * @param id    任务id
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    @RequestMapping(value = "online")
    public String online(@RequestParam(value = "id") Long id) {
        boolean result = taskService.online(id);
        if (result) {
            return ResponseUtils.success("任务发布成功");
        }
        return ResponseUtils.error("任务发布失败!");
    }

    /**
     * 下线任务
     * @param id    任务id
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    @RequestMapping(value = "offline")
    public String offline(@RequestParam(value = "id") Long id) {
        boolean result = taskService.offline(id);
        if (result) {
            return ResponseUtils.success("任务下线成功");
        }
        return ResponseUtils.error("任务下线失败");
    }

    /**
     * 获取上下游
     * @return
     */
    @RequestMapping(value = "getRelation")
    public String getRelation(@RequestParam(value = "id") long id) {
        Set<Long> upstreamSet = Sets.newHashSet();
        Set<Long> downstreamSet = Sets.newHashSet();

        List<Long> upstreamTaskIds = taskDependencyService.getUpstreamTaskIds(id);
        if(CollectionUtils.isNotEmpty(upstreamTaskIds)){
            upstreamSet.addAll(upstreamTaskIds);
        }

        List<Long> upstreamTaskIdsSnapshot = snapshotService.getUpstreamTaskIds(id);
        if(CollectionUtils.isNotEmpty(upstreamTaskIdsSnapshot)){
            upstreamSet.addAll(upstreamTaskIdsSnapshot);
        }


        List<Long> downstreamTaskIds = taskDependencyService.getDownstreamTaskIds(id);
        if(CollectionUtils.isNotEmpty(downstreamTaskIds)){
            downstreamSet.addAll(downstreamTaskIds);
        }

        List<Long> downstreamTaskIdsSnapshot = snapshotService.getDownstreamTaskIds(id);
        if(CollectionUtils.isNotEmpty(downstreamTaskIdsSnapshot)){
            downstreamSet.addAll(downstreamTaskIdsSnapshot);
        }
        //获取任务信息
        List<Task> upsteamTasks = taskService.getByIds(Lists.newArrayList(upstreamSet));
        List<Task> downsteamTasks = taskService.getByIds(Lists.newArrayList(downstreamSet));

        JSONObject result = new JSONObject();
        result.put("upstream", ModelUtils.getTaskArray(upsteamTasks));
        result.put("downstream", ModelUtils.getTaskArray(downsteamTasks));
        return ResponseUtils.success(result);
    }

    /**
     * 获取
     * @param id
     * @return
     */
    @RequestMapping(value = "getRelatedFlow")
    public String getRelatedFlow(@RequestParam(value = "id") long id) {

        Set<Long> flowIdSet = flowService.getFlowIdSetByTask(id);
        List<Flow> flows = flowService.getByIds(Lists.newArrayList(flowIdSet));
        JSONArray datas = ModelUtils.getFlowArray(flows);
        return ResponseUtils.success(datas);
    }
    /**
     * 执行
     * @param id
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    @RequestMapping(value = "run")
    public String run(@RequestParam(value = "id") long id) {

        taskService.run(id);

        return ResponseUtils.success("操作成功");
    }


}
