package cn.lite.flow.console.web.controller.flow;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.common.model.consts.TimeUnit;
import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.model.vo.DependencyVo;
import cn.lite.flow.console.common.utils.QuartzUtils;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.common.utils.TaskVersionUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.consts.TaskVersionFinalStatus;
import cn.lite.flow.console.model.query.TaskVersionQM;
import cn.lite.flow.console.service.FlowOperateService;
import cn.lite.flow.console.service.FlowService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionService;
import cn.lite.flow.console.web.annotation.AuthCheck;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 任务流相关
 * @author: yueyunyue
 * @create: 2019-03-06
 **/
@RestController
@RequestMapping("console/flow/fix")
public class TaskFlowFixController extends BaseController {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskVersionService taskVersionService;

    @Autowired
    private FlowOperateService flowOperateService;

    /**
     * 获取任务头最近的任务版本
     */
    @RequestMapping(value = "getLatestVersionNos")
    public String getLatestVersion(
               @RequestParam(value = "id") long flowId) {

        JSONArray datas = new JSONArray();
        Long headTaskId = flowService.getHeadTask(flowId);
        if(headTaskId == null){
            return ResponseUtils.error("无任务版本");
        }

        TaskVersionQM qm = new TaskVersionQM();
        qm.setTaskId(headTaskId);
        qm.addOrderDesc(TaskVersionQM.COL_VERSION_NO);
        qm.setPage(Page.getDefaultPage());

        List<TaskVersion> versions = taskVersionService.list(qm);

        if(CollectionUtils.isNotEmpty(versions)){
            for(int i = versions.size() - 1; i >=0; i --){
                datas.add(versions.get(i).getVersionNo());
            }
        }
        return ResponseUtils.success(datas);
    }
    /**
     * 获取版本dag数据
     */
    @RequestMapping(value = "viewDag")
    public String viewDag(
               @RequestParam(value = "id") long flowId,
               @RequestParam(value = "headTaskVersionNo") long headTaskVersionNo) {
        List<Long> taskIds = flowService.getTaskIds(flowId);
        if(CollectionUtils.isEmpty(taskIds)){
            return ResponseUtils.error("没有找到相关数据");
        }
        Map<Long, Task> taskInfoMap = taskService.getTaskInfo(taskIds);
        Tuple<List<TaskVersion>, List<DependencyVo>> dagViewTuple = flowOperateService.dagView(flowId, headTaskVersionNo);

        List<TaskVersion> taskVersions = dagViewTuple.getA();
        List<DependencyVo> dependencyVos = dagViewTuple.getB();
        JSONArray dependencyDatas = ModelUtils.getDependencyVoArray(dependencyVos);

        JSONArray versionDatas = new JSONArray();
        taskVersions.stream().forEach(taskVersion -> {
            Long taskId = taskVersion.getTaskId();
            Task task = taskInfoMap.get(taskId);
            JSONObject taskVersionObj = ModelUtils.getTaskVersionWithTaskObj(taskVersion, task);
            /**
             * 异常或被kill时，获取异常信息
             */
            if(taskVersion.getFinalStatus() == TaskVersionFinalStatus.FAIL.getValue()
                    || taskVersion.getFinalStatus() == TaskVersionFinalStatus.KILLED.getValue()){
                TaskInstance latestInstance = taskVersionService.getLatestInstance(taskVersion.getId());
                taskVersionObj.put(Constants.PARAM_MSG, latestInstance.getMsg());
            }

            versionDatas.add(taskVersionObj);
        });

        JSONObject result = new JSONObject();
        result.put("nodes", versionDatas);
        result.put("links", dependencyDatas);
        return ResponseUtils.success(result);
    }
    /**
     * 获取任务头任务的任务版本
     */
    @RequestMapping(value = "getHeadTaskVersionNos")
    public String getFirstTaskVersions(
               @RequestParam(value = "id") long flowId,
               @RequestParam(value = "startTime") String  startTime,
               @RequestParam(value = "endTime") String endTime) {

        Date start = DateUtils.formatToDateTime(startTime);
        Date end = DateUtils.formatToDateTime(endTime);
        if(end.before(start)){
            return ResponseUtils.error("开始时间应该小于结束时间");
        }

        JSONArray datas = new JSONArray();
        Long headTaskId = flowService.getHeadTask(flowId);
        if(headTaskId == null){
            return ResponseUtils.error("无任务版本");
        }

        Task task = taskService.getById(headTaskId);
        TimeUnit timeUnit = TimeUnit.getType(task.getPeriod());
        //计算时间区间运行时间
        List<Date> taskRunTimes = QuartzUtils.getRunDateTimes(QuartzUtils.completeCrontab(task.getCronExpression()), start, end);
        if (CollectionUtils.isEmpty(taskRunTimes)) {
            return ResponseUtils.error("无任务版本");
        }
        taskRunTimes.forEach(runTime -> {
            Long taskVersionNo = TaskVersionUtils.getTaskVersion(runTime, timeUnit);
            datas.add(taskVersionNo);
        });
        return ResponseUtils.success(datas);
    }
    /**
     * 修复整个任务流
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_FLOW)
    @RequestMapping(value = "fixFlow")
    public String fixFlow(
            @RequestParam(value = "id") long flowId,
            @RequestParam(value = "headTaskVersionNo") long headTaskVersionNo) {
         flowOperateService.fix(flowId, headTaskVersionNo);
        return ResponseUtils.success("操作成功");
    }

    /**
     * 获取版本dag数据
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_FLOW)
    @RequestMapping(value = "fixFromNode")
    public String fix(
            @RequestParam(value = "id") long flowId,
            @RequestParam(value = "fixVersionId") long fixVersionId,
            @RequestParam(value = "headTaskVersionNo") long headTaskVersionNo) {
         flowOperateService.fixFromNode(flowId, headTaskVersionNo, fixVersionId);
        return ResponseUtils.success("操作成功");
    }


}
