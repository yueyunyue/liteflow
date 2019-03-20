package cn.lite.flow.console.web.controller.instance;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskInstanceDependency;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.consts.TaskVersionStatus;
import cn.lite.flow.console.model.query.TaskInstanceDependencyQM;
import cn.lite.flow.console.model.query.TaskInstanceQM;
import cn.lite.flow.console.model.query.TaskQM;
import cn.lite.flow.console.service.TaskInstanceDependencyService;
import cn.lite.flow.console.service.TaskInstanceService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionService;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import cn.lite.flow.executor.client.ExecutorJobRpcService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by luya on 2018/11/19.
 */
@RestController("taskInstanceController")
@RequestMapping("console/instance")
@AuthCheckIgnore
public class TaskInstanceController extends BaseController {

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskInstanceDependencyService taskInstanceDependencyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ExecutorJobRpcService executorJobRpcService;

    @Autowired
    private TaskVersionService taskVersionService;

    /**
     * 实例列表
     *
     * @param taskId
     * @param taskVersionId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list")
    public String list(
           @RequestParam(value = "taskId", required = false) Long taskId,
           @RequestParam(value = "taskVersionId", required = false) Long taskVersionId,
           @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        TaskInstanceQM qm = new TaskInstanceQM();
        qm.setTaskId(taskId);
        qm.setVersionId(taskVersionId);
        qm.setPage(pageNum, pageSize);
        qm.addOrderDesc(TaskInstanceQM.COL_ID);

        List<TaskInstance> taskInstanceList = taskInstanceService.list(qm);

        int total = 0;
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(taskInstanceList)) {
            total = taskInstanceService.count(qm);
            taskInstanceList.forEach(instance -> {
                JSONObject obj = ModelUtils.getTaskInstanceObj(instance);
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 依赖
     * @param instanceId        实例id
     * @param pageNum           当前页码
     * @param pageSize          每页数量
     * @return
     */
    @RequestMapping(value = "dependencies")
    public String dependencies(
            @RequestParam(value = "id") Long instanceId,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        TaskInstanceDependencyQM qm = new TaskInstanceDependencyQM();
        qm.setInstanceId(instanceId);
        qm.setPage(pageNum, pageSize);
        qm.addOrderDesc(TaskInstanceDependencyQM.COL_ID);

        List<TaskInstanceDependency> taskInstanceDependencyList = taskInstanceDependencyService.list(qm);

        int total = 0;
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(taskInstanceDependencyList)) {
            total = taskInstanceDependencyService.count(qm);

            List<Long> upstreamTaskIds = taskInstanceDependencyList
                    .stream()
                    .map(TaskInstanceDependency::getUpstreamTaskId)
                    .distinct()
                    .collect(Collectors.toList());

            TaskQM taskQM = new TaskQM();
            taskQM.setIds(upstreamTaskIds);
            List<Task> taskList = taskService.list(taskQM);

            Map<Long, String> taskInfo = Collections.EMPTY_MAP;
            if (CollectionUtils.isNotEmpty(taskList)) {
                taskInfo = taskList.stream().collect(Collectors.toMap(Task::getId, Task::getName));
            }

            Map<Long, String> finalTaskInfo = taskInfo;
            taskInstanceDependencyList.forEach(dependency -> {
                JSONObject obj = new JSONObject();
                obj.put("id", dependency.getId());

                /**依赖的任务信息*/
                obj.put("upstreamTaskId", dependency.getUpstreamTaskId());
                obj.put("upstreamTaskName", finalTaskInfo.get(dependency.getUpstreamTaskId()));

                /**获取依赖的任务版本的状态*/
                TaskVersion taskVersion = taskVersionService.getTaskVersion(dependency.getUpstreamTaskId(), dependency.getUpstreamTaskVersionNo());
                if(taskVersion != null){
                    JSONObject taskVersionObj = ModelUtils.getTaskVersionObj(taskVersion);
                    TaskInstance taskInstance = taskVersionService.getLatestInstance(taskVersion.getId());
                    taskVersionObj.put("logicRunTime", taskInstance == null ? "" : taskInstance.getLogicRunTime());
                    taskVersionObj.put("msg", taskInstance == null ? "" : taskInstance.getMsg());
                    obj.put("upstreamTaskVersion", taskVersionObj);
                }

                obj.put("status", dependency.getStatus());
                obj.put("createTime", dependency.getCreateTime());
                obj.put("updateTime", dependency.getUpdateTime());
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 运行日志
     *
     * @param instanceId        实例id
     * @return
     */
    @RequestMapping(value = "log")
    public String log(
            @RequestParam(value = "id") Long instanceId
    ) {

        TaskInstance taskInstance = taskInstanceService.getById(instanceId);
        Preconditions.checkArgument(taskInstance != null, "未获取到相关实例");
        return ResponseUtils.success(taskInstanceService.getLog(instanceId));
    }

    /**
     * kill操作
     *
     * @param instanceId        实例id
     * @return
     */
    @RequestMapping(value = "kill")
    public String kill(@RequestParam(value = "id") Long instanceId) {
        TaskInstance taskInstance = taskInstanceService.getById(instanceId);

        Preconditions.checkArgument(taskInstance != null, "未获取到相关实例");
        int currStatus = taskInstance.getStatus();
        if (!(TaskVersionStatus.READY.getValue() == currStatus || TaskVersionStatus.RUNNING.getValue() == currStatus)) {
            throw new ConsoleRuntimeException("该实例目前不是[就绪]或[运行状态], 不能做kill操作");
        }

        if (taskInstanceService.kill(instanceId)) {
            return ResponseUtils.success("操作成功");
        }
        return ResponseUtils.error("操作失败");
    }

    /**
     * 置为成功状态
     *
     * @param instanceId        实例id
     * @return
     */
    @RequestMapping(value = "ignore")
    public String ignore(@RequestParam(value = "id") Long instanceId) {
        TaskInstance taskInstance = taskInstanceService.getById(instanceId);

        Preconditions.checkArgument(taskInstance != null, "未查询到相关的实例");
        Preconditions.checkArgument(taskInstance.getStatus() == TaskVersionStatus.FAIL.getValue(),
                "该实例的状态为非失败状态, 不能进行该操作");

        TaskInstance updateInstance = new TaskInstance();
        updateInstance.setId(instanceId);
        updateInstance.setStatus(TaskVersionStatus.SUCCESS.getValue());

        int result = taskInstanceService.updateWithStatus(updateInstance, TaskVersionStatus.FAIL.getValue());
        if (result == 1) {
            return ResponseUtils.success("操作成功");
        }
        return ResponseUtils.error("操作失败");
    }

    /**
     * 实例依赖置为无效状态
     * @return
     */
    @RequestMapping(value = "dependency/off")
    public String offDependency(@RequestParam(value = "id") long id) {
        TaskInstanceDependency instanceDependency = new TaskInstanceDependency();
        instanceDependency.setId(id);
        instanceDependency.setStatus(StatusType.OFF.getValue());
        taskInstanceDependencyService.update(instanceDependency);
        return ResponseUtils.success("操作成功");
    }
    /**
     * 实例依赖置为有效状态
     * @return
     */
    @RequestMapping(value = "dependency/on")
    public String onDependency(@RequestParam(value = "id") long id) {
        TaskInstanceDependency instanceDependency = new TaskInstanceDependency();
        instanceDependency.setId(id);
        instanceDependency.setStatus(StatusType.ON.getValue());
        taskInstanceDependencyService.update(instanceDependency);
        return ResponseUtils.success("操作成功");
    }
}
