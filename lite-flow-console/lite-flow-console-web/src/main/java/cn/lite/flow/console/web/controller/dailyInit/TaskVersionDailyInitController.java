package cn.lite.flow.console.web.controller.dailyInit;

import cn.lite.flow.console.common.enums.TargetTypeEnum;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskVersionDailyInit;
import cn.lite.flow.console.model.query.TaskVersionDailyInitQM;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionDailyInitService;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import cn.lite.flow.console.web.controller.BaseController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 任务初始化版本相关
 * @author: ly
 * @create: 2019-03-27
 */
@RestController
@RequestMapping("console/dailyInit")
public class TaskVersionDailyInitController extends BaseController {

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;

    @Autowired
    private TaskVersionDailyInitService taskVersionDailyInitService;

    @Autowired
    private TaskService taskService;

    /**
     * 任务初始化版本列表
     *
     * @param taskId        任务id
     * @param day           日期
     * @param status        状态
     * @param pageNum       当前页码
     * @param pageSize      每页数量
     * @return
     */
    @RequestMapping(value = "list")
    public String list(
            @RequestParam(value = "taskId", required = false) Long taskId,
            @RequestParam(value = "day", required = false) Long day,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {

        SessionUser user = getUser();

        int total = 0;
        JSONArray datas = new JSONArray();

        List<Long> taskIds = null;
        if (!user.getIsSuper()) {
            /**不是超级管理员，则只能查看自己有权限的*/
            taskIds = userGroupAuthMidService.getTargetId(user.getId(), user.getGroupIds(), TargetTypeEnum.TARGET_TYPE_TASK.getCode());
            if (CollectionUtils.isEmpty(taskIds)) {
                return ResponseUtils.list(total, datas);
            }
            if (taskId != null && !taskIds.contains(taskId)) {
                throw new ConsoleRuntimeException("您没有该任务的权限");
            }
        }

        TaskVersionDailyInitQM queryModel = new TaskVersionDailyInitQM();
        queryModel.setInTaskIds(taskIds);
        queryModel.setTaskId(taskId);
        queryModel.setDay(day);
        queryModel.setStatus(status);
        queryModel.addOrderDesc(TaskVersionDailyInitQM.COL_ID);
        queryModel.setPage(pageNum, pageSize);

        List<TaskVersionDailyInit> taskVersionDailyInitList = taskVersionDailyInitService.list(queryModel);

        if (CollectionUtils.isNotEmpty(taskVersionDailyInitList)) {
            total = taskVersionDailyInitService.count(queryModel);

            List<Long> taskIdList = taskVersionDailyInitList
                    .stream()
                    .map(TaskVersionDailyInit::getTaskId)
                    .distinct()
                    .collect(Collectors.toList());
            List<Task> taskList = taskService.getByIds(taskIdList);

            Map<Long, String> taskInfo = null;
            if (CollectionUtils.isNotEmpty(taskIdList)) {
                taskInfo = taskList.stream().collect(Collectors.toMap(Task::getId, Task::getName));
            }

            Map<Long, String> finalTaskInfo = taskInfo;
            taskVersionDailyInitList.forEach(dailyInit -> {
                JSONObject obj = new JSONObject();
                obj.put("id", dailyInit.getId());
                obj.put("taskId", dailyInit.getTaskId());
                obj.put("taskName", finalTaskInfo.get(dailyInit.getTaskId()));
                obj.put("day", dailyInit.getDay());
                obj.put("status", dailyInit.getStatus());
                obj.put("msg", dailyInit.getMsg());
                obj.put("createTime", dailyInit.getCreateTime());
                obj.put("updateTime", dailyInit.getUpdateTime());
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);

    }

}
