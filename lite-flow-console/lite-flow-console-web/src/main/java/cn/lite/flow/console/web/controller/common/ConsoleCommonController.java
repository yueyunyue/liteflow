package cn.lite.flow.console.web.controller.common;

import cn.lite.flow.console.common.consts.TimeUnit;
import cn.lite.flow.console.common.enums.TargetTypeEnum;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.consts.TaskStatus;
import cn.lite.flow.console.model.consts.TaskVersionStatus;
import cn.lite.flow.console.model.query.TaskQM;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import cn.lite.flow.executor.client.ExecutorContainerRpcService;
import cn.lite.flow.executor.client.ExecutorPluginRpcService;
import cn.lite.flow.executor.model.basic.ExecutorContainer;
import cn.lite.flow.executor.model.basic.ExecutorPlugin;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/12/14.
 */
@RestController("consoleCommonController")
@RequestMapping("console/common")
@AuthCheckIgnore
public class ConsoleCommonController extends BaseController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;

    /**
     * 获取所有有权限的任务
     * @return
     */
    @RequestMapping(value = "getAllAuthTask")
    public String getAllAuthTask() {
        SessionUser user = getUser();
        JSONArray datas = new JSONArray();
        List<Long> taskIds = null;
        if (!user.getIsSuper()) {
            /**不是超级管理员，则只能查看自己的*/
            taskIds = userGroupAuthMidService.getTargetId(user.getId(), user.getGroupIds(), TargetTypeEnum.TARGET_TYPE_TASK.getCode());
            if (CollectionUtils.isEmpty(taskIds)) {
                return ResponseUtils.success(datas);
            }
        }

        TaskQM taskQM = new TaskQM();
        taskQM.setIds(taskIds);
        List<Task> taskList = taskService.list(taskQM);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> {
                JSONObject obj = ModelUtils.getTaskObj(task);
                datas.add(obj);
            });
        }

        return ResponseUtils.success(datas);
    }

    /**
     * 获取所有的任务状态
     *
     * @return
     */
    @RequestMapping(value = "getAllTaskStatus")
    public String getAllTaskStatus() {
        JSONArray datas = new JSONArray();
        for (TaskStatus model : TaskStatus.values()) {
            JSONObject obj = new JSONObject();
            obj.put("value", model.getValue());
            obj.put("desc", model.getDesc());
            datas.add(obj);
        }
        return ResponseUtils.success(datas);
    }

    /**
     * 获取所有的任务版本状态
     *
     * @return
     */
    @RequestMapping(value = "getAllTaskVersionStatus")
    public String getAllTaskVersionStatus() {
        JSONArray datas = new JSONArray();
        for (TaskVersionStatus model : TaskVersionStatus.values()) {
            JSONObject obj = new JSONObject();
            obj.put("value", model.getValue());
            obj.put("desc", model.getDesc());
            datas.add(obj);
        }
        return ResponseUtils.success(datas);
    }


    /**
     * 获取所有的时间枚举/周期
     *
     * @return
     */
    @RequestMapping(value = "getAllPeriod")
    public String getAllPeriod() {
        JSONArray datas = new JSONArray();
        for (TimeUnit model : TimeUnit.values()) {
            JSONObject obj = new JSONObject();
            obj.put("value", model.getValue());
            obj.put("suffix", model.getSuffix());
            obj.put("desc", model.getDesc());
            obj.put("versionExpression", model.getVersionExpression());
            datas.add(obj);
        }
        return ResponseUtils.success(datas);
    }

}
