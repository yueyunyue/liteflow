package cn.lite.flow.console.web.controller.version;

import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.query.TaskInstanceQM;
import cn.lite.flow.console.model.query.TaskVersionQM;
import cn.lite.flow.console.service.TaskInstanceDependencyService;
import cn.lite.flow.console.service.TaskInstanceService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionService;
import cn.lite.flow.console.web.annotation.AuthCheck;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 任务相关
 * @author: yueyunyue
 * @create: 2018-12-25
 **/
@RestController
@RequestMapping("console/version")
public class TaskVersionController extends BaseController {

    private final static Logger LOG = LoggerFactory.getLogger(TaskVersionController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskVersionService taskVersionService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskInstanceDependencyService instanceDependencyService;

    /**
     * 任务版本列表
     *
     * @param taskId   任务id
     * @param pageNum  当前页码
     * @param pageSize 每页数量
     * @return
     */
    @RequestMapping(value = "list")
    public String list(
            @RequestParam(value = "taskId", required = false) Long taskId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "finalStatus", required = false) Integer finalStatus,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        TaskVersionQM qm = new TaskVersionQM();
        qm.setTaskId(taskId);
        qm.setStatus(status);
        qm.setFinalStatus(finalStatus);
        qm.setPage(pageNum, pageSize);
        qm.addOrderDesc(TaskVersionQM.COL_ID);

        List<TaskVersion> taskVersionList = taskVersionService.list(qm);

        int total = 0;
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(taskVersionList)) {
            total = taskVersionService.count(qm);

            List<Long> taskIdList = taskVersionList
                    .stream()
                    .map(TaskVersion::getTaskId)
                    .distinct()
                    .collect(Collectors.toList());
            List<Task> taskList = taskService.getByIds(taskIdList);

            Map<Long, String> taskInfo = null;
            if (CollectionUtils.isNotEmpty(taskList)) {
                taskInfo = taskList.stream().collect(Collectors.toMap(Task::getId, Task::getName));
            }

            Map<Long, String> finalTaskInfo = taskInfo;
            taskVersionList.forEach(version -> {
                JSONObject obj = ModelUtils.getTaskVersionObj(version);
                obj.put("taskName", finalTaskInfo.get(version.getTaskId()));
                TaskInstance taskInstance = taskVersionService.getLatestInstance(version.getId());
                obj.put("latestInstance", ModelUtils.getTaskInstanceObj(taskInstance));
                obj.put("msg", taskInstance == null ? "" : taskInstance.getMsg());
                datas.add(obj);
            });

        }

        return ResponseUtils.list(total, datas);
    }

    /**
     * 任务版本对应的实例
     *
     * @param id       任务版本id
     * @param pageNum  当前页码
     * @param pageSize 每页数量
     * @return
     */
    @RequestMapping(value = "instance")
    public String instance(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        TaskInstanceQM qm = new TaskInstanceQM();
        qm.setVersionId(id);
        qm.setPage(pageNum, pageSize);
        qm.addOrderDesc(TaskInstanceQM.COL_LOGIC_RUN_TIME);

        List<TaskInstance> taskInstanceList = taskInstanceService.list(qm);

        int total = 0;
        JSONArray datas = new JSONArray();

        if (CollectionUtils.isNotEmpty(taskInstanceList)) {
            total = taskInstanceService.count(qm);
            taskInstanceList.forEach(instance -> {
                JSONObject obj = new JSONObject();
                obj.put("id", instance.getId());
                obj.put("taskId", instance.getTaskId());
                obj.put("taskVersionId", instance.getTaskVersionId());
                obj.put("taskVersionNo", instance.getTaskVersionNo());
                obj.put("logicRunTime", instance.getLogicRunTime());
                obj.put("pluginConf", instance.getPluginConf());
                obj.put("status", instance.getStatus());
                obj.put("runStartTime", instance.getRunStartTime());
                obj.put("runEndTime", instance.getRunEndTime());
                obj.put("msg", instance.getMsg());
                obj.put("executorJobId", instance.getExecutorJobId());
                obj.put("createTime", instance.getCreateTime());
                obj.put("updateTime", instance.getUpdateTime());
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 修复版本
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "fix")
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK_VERSION, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    public String fix(@RequestParam(value = "id") long id) {

        taskVersionService.fix(id);

        return ResponseUtils.success("");
    }

    /**
     * 深度修复，从错误节点修复直到叶子节点
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "deepFix")
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK_VERSION, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    public String deepFix(@RequestParam(value = "id") long id) {
        taskVersionService.deepFix(id);
        return ResponseUtils.success("");
    }


    /**
     * 忽略版本
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "ignore")
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK_VERSION)
    public String ignore(@RequestParam(value = "id") long id) {
        taskVersionService.ignore(id);
        return ResponseUtils.success("");
    }

    /**
     * 杀死
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "kill")
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK_VERSION, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    public String kill(@RequestParam(value = "id") long id) {
        taskVersionService.kill(id);
        return ResponseUtils.success("");
    }

    /**
     * 通过区间修复数据
     *
     * @param taskId
     * @return
     */
    @RequestMapping(value = "rangeFix")
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_TASK_VERSION, operateType = OperateTypeEnum.OPERATE_TYPE_EXECUTE)
    public String rangeFix(@RequestParam(value = "taskId") long taskId,
                           @RequestParam(value = "startTime") String startTime,
                           @RequestParam(value = "endTime") String endTime) {


        Date start = DateUtils.formatToDateTime(startTime);
        Date end = DateUtils.formatToDateTime(endTime);
        if (end.before(start)) {
            return ResponseUtils.error("开始时间应该小于结束时间");
        }

        taskVersionService.calVersionAndInstanceWithDependency(taskId, start, end);

        return ResponseUtils.success("");
    }

    /**
     * 获取日志
     * @param versionId
     * @return
     */
    @RequestMapping(value = "log")
    public String log(
            @RequestParam(value = "id") Long versionId
    ) {
        TaskInstance latestInstance = taskVersionService.getLatestInstance(versionId);
        return ResponseUtils.success(taskInstanceService.getLog(latestInstance.getId()));
    }


}
