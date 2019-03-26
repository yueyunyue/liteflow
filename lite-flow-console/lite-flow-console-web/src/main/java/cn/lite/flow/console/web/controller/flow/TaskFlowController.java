package cn.lite.flow.console.web.controller.flow;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.common.consts.TimeUnit;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.TargetTypeEnum;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.utils.DagUtils;
import cn.lite.flow.console.common.utils.ParamExpressionUtils;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.common.utils.TaskVersionUtils;
import cn.lite.flow.console.model.basic.*;
import cn.lite.flow.console.model.consts.FlowStatus;
import cn.lite.flow.console.model.consts.TaskDependencyType;
import cn.lite.flow.console.model.query.FlowQM;
import cn.lite.flow.console.model.query.TaskInstanceDependencyQM;
import cn.lite.flow.console.service.*;
import cn.lite.flow.console.web.annotation.AuthCheck;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 任务流相关
 * @author: yueyunyue
 * @create: 2018-07-25
 **/
@RestController
@RequestMapping("console/flow")
public class TaskFlowController extends BaseController {

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowDependencySnapshotService flowDependencySnapshotService;

    @Autowired
    private FlowDependencyService flowDependencyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DagService dagService;

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;


    /**
     * 获取任务流列表
     *
     * @param nameLike 名称模糊查询
     * @param status   状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return
     */
    @RequestMapping(value = "list")
    public String list(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "nameLike", required = false) String nameLike,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {

        int total = 0;
        JSONArray datas = new JSONArray();

        SessionUser user = getUser();
        List<Long> flowIds = null;
        if (!user.getIsSuper()) {
            /**如果不是超级管理员，则只能查看自己权限范围内的*/
            flowIds = userGroupAuthMidService.getTargetId(user.getId(),
                    user.getGroupIds(), TargetTypeEnum.TARGET_TYPE_FLOW.getCode());
            if (CollectionUtils.isEmpty(flowIds)) {
                return ResponseUtils.list(total, datas);
            }
        }

        FlowQM flowQM = new FlowQM();
        flowQM.setIds(flowIds);
        flowQM.setId(id);
        flowQM.setNameLike(nameLike);
        flowQM.setStatus(status);
        flowQM.setPage(pageNum, pageSize);
        flowQM.addOrderDesc(TaskInstanceDependencyQM.COL_ID);

        List<Flow> flowList = flowService.list(flowQM);

        if (CollectionUtils.isNotEmpty(flowList)) {

            List<Long> userIds = flowList
                    .stream()
                    .map(Flow::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, String> userIdAndName = getUserName(userIds);

            total = flowService.count(flowQM);
            flowList.forEach(flow -> {
                JSONObject obj = ModelUtils.getFlowObj(flow);
                setUserInfo(obj, flow.getUserId(), userIdAndName);
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 添加任务流
     *
     * @param name        名称
     * @param description 说明
     * @return
     */
    @RequestMapping(value = "add")
    public String add(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description
    ) {
        FlowQM flowQM = new FlowQM();
        flowQM.setName(name);
        List<Flow> flowList = flowService.list(flowQM);
        if (CollectionUtils.isNotEmpty(flowList)) {
            throw new ConsoleRuntimeException("该名称已经存在,请更换后再添加");
        }

        Flow flow = new Flow();
        flow.setName(name);
        flow.setDescription(description);
        flow.setUserId(getUser().getId());
        flowService.add(flow);
        return ResponseUtils.success("添加任务流成功");
    }

    /**
     * 编辑
     *
     * @param id          id
     * @param name        名称
     * @param description 说明
     * @return
     */
    @RequestMapping(value = "edit")
    public String edit(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description
    ) {
        FlowQM flowQM = new FlowQM();
        flowQM.setName(name);
        List<Flow> flowList = flowService.list(flowQM);
        if (CollectionUtils.isNotEmpty(flowList) && !flowList.get(0).getId().equals(id)) {
            throw new ConsoleRuntimeException("该名称已经存在,请更换后再添加");
        }

        Flow flow = new Flow();
        flow.setId(id);
        flow.setName(name);
        flow.setDescription(description);
        flowService.update(flow);
        return ResponseUtils.success("编辑成功");
    }

    /**
     * 任务流上下游依赖
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "viewDag")
    public String viewDag(@RequestParam(value = "id") Long id) {

        Tuple<List<Task>, List<TaskDependency>> flowDagData = dagService.getFlowDagData(id);

        JSONObject data = new JSONObject();
        if (flowDagData != null) {
            data.put("nodes", flowDagData.getA());
            data.put("links", flowDagData.getB());
        }
        return ResponseUtils.success(data);
    }

    /**
     * 编辑任务流的依赖
     *
     * @param id    任务流id
     * @param links 任务依赖
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_FLOW)
    @RequestMapping(value = "editLinks")
    public String editLinks(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "links") String links
    ) {
        Flow flow = flowService.getById(id);
        if (flow == null) {
            throw new ConsoleRuntimeException("未查询到相关的任务流");
        }
        JSONArray datas = JSON.parseArray(links);
        if (datas == null) {
            throw new ConsoleRuntimeException("添加的任务依赖不合法");
        }
        List<TaskDependency> taskDependencies = Lists.newArrayList();

        //用来验证参数配置是否正确
        Long testTaskVersion = TaskVersionUtils.getTaskVersion(DateUtils.getNow(), TimeUnit.DAY);
        String testTaskVersionStr = String.valueOf(testTaskVersion);

        for (int i = 0; i < datas.size(); i++) {
            JSONObject data = datas.getJSONObject(i);
            Long taskId = data.getLong("taskId");
            Long upstreamId = data.getLong("upstreamTaskId");
            Integer type = data.getInteger("type");
            if (type == null) {
                type = 0;
            }
            String conf = data.getString("config");

            /**
             * 校验config配置
             */
            if (type != null) {
                if (type == TaskDependencyType.TIME_RANGE.getValue()) {
                    boolean checkPass = true;
                    if (StringUtils.isBlank(conf)) {
                        checkPass = false;
                    }
                    String expression = ParamExpressionUtils.handleTimeExpression(conf, testTaskVersionStr);
                    if (StringUtils.equals(expression, conf)) {
                        checkPass = false;
                    }
                    if (!checkPass) {
                        return ResponseUtils.error("依赖{}->{}关系配置有误");
                    }
                }else if (type == TaskDependencyType.OFFSET.getValue()) {
                    try {
                        Integer.parseInt(conf);
                    }catch (Throwable e){
                        return ResponseUtils.error("依赖{}->{}关系偏移量配置有误");
                    }
                }
            }

            TaskDependency taskDependency = new TaskDependency();
            taskDependency.setTaskId(taskId);
            taskDependency.setUpstreamTaskId(upstreamId);
            taskDependency.setType(type);
            taskDependency.setConfig(conf);
            taskDependencies.add(taskDependency);
        }

        /**
         * 1.校验依赖之间是否有环
         * 2.如果任务流已经上线，则更新任务依赖
         * 3.如果任务流是新建 or 下线状态，则更新任务流快照
         */
        List<Long> taskIds = DagUtils.getTaskIds(taskDependencies);
        DagUtils.getDagLevel(taskDependencies, taskIds);
        if (FlowStatus.ONLINE.getValue() == flow.getStatus()) {
            flowService.addOrUpdateDependencies(id, taskDependencies);
        } else {
            flowDependencySnapshotService.clearAndAdd(id, taskDependencies);
        }
        return ResponseUtils.success("编辑成功");
    }

    /**
     * 发布任务流
     *
     * @param id 任务流id
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_FLOW)
    @RequestMapping(value = "online")
    public String online(@RequestParam(value = "id") Long id) {
        Tuple<Boolean, List<String>> result = flowService.online(id);
        if (result.getA()) {
            if (CollectionUtils.isNotEmpty(result.getB())) {
                return ResponseUtils.success(Joiner.on(",").join(result.getB()));
            } else {
                return ResponseUtils.success("上线成功");
            }
        }
        return ResponseUtils.error(Joiner.on(",").join(result.getB()));
    }

    /**
     * 下线任务流
     *
     * @param id 任务流id
     * @return
     */
    @AuthCheck(checkType = AuthCheckTypeEnum.AUTH_CHECK_FLOW)
    @RequestMapping(value = "offline")
    public String offline(Long id) {
        Tuple<Boolean, List<String>> result = flowService.offline(id);
        if (result.getA()) {
            if (CollectionUtils.isNotEmpty(result.getB())) {
                return ResponseUtils.success(Joiner.on(",").join(result.getB()));
            } else {
                return ResponseUtils.success("下线成功");
            }
        }
        return ResponseUtils.error(Joiner.on(",").join(result.getB()));
    }
}
