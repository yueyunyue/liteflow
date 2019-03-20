package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.exception.CommonRuntimeException;
import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.consts.BooleanType;
import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;
import cn.lite.flow.console.common.enums.SourceTypeEnum;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.utils.DagUtils;
import cn.lite.flow.console.common.utils.SessionContext;
import cn.lite.flow.console.dao.mapper.FlowMapper;
import cn.lite.flow.console.model.basic.Flow;
import cn.lite.flow.console.model.basic.FlowDependency;
import cn.lite.flow.console.model.basic.FlowDependencySnapshot;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.basic.UserGroupAuthMid;
import cn.lite.flow.console.model.consts.FlowStatus;
import cn.lite.flow.console.model.consts.TaskStatus;
import cn.lite.flow.console.model.query.FlowQM;
import cn.lite.flow.console.service.FlowDependencyService;
import cn.lite.flow.console.service.FlowDependencySnapshotService;
import cn.lite.flow.console.service.FlowService;
import cn.lite.flow.console.service.TaskDependencyService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import cn.lite.flow.console.service.auth.AuthCheckService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by luya on 2018/7/23.
 */
@Service
public class FlowServiceImpl implements FlowService {

    private final static Logger LOG = LoggerFactory.getLogger(FlowServiceImpl.class);

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private FlowDependencySnapshotService snapshotService;

    @Autowired
    private FlowDependencyService flowDependencyService;

    @Autowired
    private TaskDependencyService dependencyService;

    @Autowired
    private TaskService taskService;

    @Qualifier("taskAuthCheckServiceImpl")
    @Autowired
    private AuthCheckService taskAuthCheckService;

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;

    @Override
    @Transactional("consoleTxManager")
    public void add(Flow model) {
        model.setStatus(FlowStatus.NEW.getValue());
        flowMapper.insert(model);

        /**
         * 创建人拥有所有权限
         */
        UserGroupAuthMid userGroupAuthMid = new UserGroupAuthMid();
        userGroupAuthMid.setSourceId(model.getUserId());
        userGroupAuthMid.setSourceType(SourceTypeEnum.SOURCE_TYPE_USER.getCode());
        userGroupAuthMid.setTargetId(model.getId());
        userGroupAuthMid.setTargetType(AuthCheckTypeEnum.AUTH_CHECK_FLOW.getCode());
        userGroupAuthMid.setHasEditAuth(BooleanType.TRUE.getValue());
        userGroupAuthMid.setHasExecuteAuth(BooleanType.TRUE.getValue());
        userGroupAuthMid.setUserId(model.getUserId());
        userGroupAuthMidService.add(userGroupAuthMid);
    }

    @Override
    public Flow getById(long id) {
        return flowMapper.getById(id);
    }

    @Override
    public int update(Flow model) {
        return flowMapper.update(model);
    }

    @Override
    public int count(FlowQM queryModel) {
        return flowMapper.count(queryModel);
    }

    @Override
    public List<Flow> list(FlowQM queryModel) {
        return flowMapper.findList(queryModel);
    }

    @Override
    public Long getHeadTask(long flowId) {
        List<TaskDependency> dependencies = this.getDependencies(flowId);
        if(CollectionUtils.isNotEmpty(dependencies)){
            List<Long> headTaskIds = DagUtils.getHeadTaskIds(dependencies, DagUtils.getTaskIds(dependencies));
            if(CollectionUtils.isNotEmpty(headTaskIds)){
                return headTaskIds.get(0);
            }
        }
        return null;
    }

    @Override
    public List<Flow> getByIds(List<Long> flowIds) {

        if(CollectionUtils.isEmpty(flowIds)){
            return null;
        }
        List<Flow> flows = Lists.newArrayList();
        List<List<Long>> partitionIds = Lists.partition(flowIds, CommonConstants.LIST_BATCH_SIZE);
        partitionIds.forEach(fids -> {
            FlowQM queryModel = new FlowQM();
            queryModel.setIds(fids);
            List<Flow> flowList = flowMapper.findList(queryModel);
            if(CollectionUtils.isNotEmpty(flowList)){
                flows.addAll(flowList);
            }
        });
        return flows;
    }

    @Override
    public List<TaskDependency> getDependencies(long flowId) {

        Flow flow = flowMapper.getById(flowId);
        /**
         * 上线时，依赖放在task_dependency中
         */
        if (flow.getStatus() == FlowStatus.ONLINE.getValue()) {

            return flowDependencyService.getDependencies(flowId);
            /**
             * 新建或下线状态从快照表中取
             */
        } else if (flow.getStatus() == FlowStatus.NEW.getValue()
                || flow.getStatus() == FlowStatus.OFFLINE.getValue()) {
            return snapshotService.getDependencies(flowId);
        }

        return null;
    }

    @Override
    public List<Long> getTaskIds(long flowId) {

        List<TaskDependency> dependencies = this.getDependencies(flowId);
        return DagUtils.getTaskIds(dependencies);
    }

    private Tuple<Set<Long>, Set<Long>> getSharedAndUnique(long flowId){
        //共享的依赖
        Set<Long> sharedDependency = Sets.newHashSet();
        //仅仅属于当前任务流的依赖
        Set<Long> flowUniqueDependency = Sets.newHashSet();
        /**
         * 任务流原来的依赖关系
         * 1.筛选出共享依赖和独享依赖
         */
        List<FlowDependency> flowOriginalDependencies = flowDependencyService.getFlowDependencies(flowId);
        if (CollectionUtils.isNotEmpty(flowOriginalDependencies)) {
            for (FlowDependency flowDependency : flowOriginalDependencies) {
                if (flowDependencyService.isFlowUnique(flowDependency.getTaskDependencyId(), flowId)) {
                    flowUniqueDependency.add(flowDependency.getTaskDependencyId());
                } else {
                    sharedDependency.add(flowDependency.getTaskDependencyId());
                }
            }
        }
        return new Tuple<>(sharedDependency, flowUniqueDependency);
    }

    /**
     * 用户是否有执行权限
     * @param user
     * @param taskId
     * @return
     */
    private boolean hasTaskExecuteAuth(SessionUser user, long taskId){
        if(user.getIsSuper()){
            return true;
        }
        boolean userAuth = taskAuthCheckService.checkUserAuth(user.getId(), taskId, OperateTypeEnum.OPERATE_TYPE_EXECUTE);
        boolean groupAuth = taskAuthCheckService.checkUserGroupAuth(user.getGroupIds(), taskId, OperateTypeEnum.OPERATE_TYPE_EXECUTE);
        return userAuth || groupAuth;
    }

    @Override
    @Transactional("consoleTxManager")
    public Tuple<Boolean, List<String>> online(long flowId) {
        SessionUser user = SessionContext.getUser();

        Flow flow = flowMapper.getById(flowId);
        List<TaskDependency> dependencies = this.getDependencies(flowId);
        if(CollectionUtils.isEmpty(dependencies)){
            throw new ConsoleRuntimeException("任务流没有任务");
        }
        /**
         * 新建或下线状态时，任务流依赖存在于快照中
         */
        if (flow.getStatus() == FlowStatus.NEW.getValue() || flow.getStatus() == FlowStatus.OFFLINE.getValue()) {
            snapshotService.deleteFlowSnapshot(flowId);
            this.addOrUpdateDependencies(flowId, dependencies);
        }

        List<Long> taskIds = DagUtils.getTaskIds(dependencies);

        List<List<Long>> dagTaskIdLevel = DagUtils.getTaskIdDagLevel(dependencies, taskIds);

        /**
         * 处理任务上线
         */
        Tuple<Set<Long>, Set<Long>> sharedAndUnique = getSharedAndUnique(flowId);
        //共享的依赖
        Set<Long> sharedDependency = sharedAndUnique.getA();
        /**
         * 通过共享依赖获取部分共享任务
         */
        Set<Long> sharedTaskSet = getFlowSharedTask(flowId, taskIds, sharedDependency);

        boolean canOnline = true;
        List<String> results = Lists.newArrayList();
        /**
         * 自上而下上线任务
         */
        List<Long> canOnlineTaskIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dagTaskIdLevel)) {

            Set<Long> aleadyOnlineId = Sets.newHashSet();
            for (List<Long> levelTaskIds : dagTaskIdLevel) {

                if(!canOnline){
                    break;
                }
                for (Long taskId : levelTaskIds) {
                    try {
                        Task task = taskService.getById(taskId);
                        /**
                         * 已上线任务，忽略
                         */
                        if (task.getStatus() == TaskStatus.ONLINE.getValue()) {
                            aleadyOnlineId.add(taskId);
                            continue;
                        }

                        /**
                         * 共享任务，忽略
                         */
                        if (sharedTaskSet.contains(taskId)) {
                            canOnline = false;
                            results.add(String.format("任务%d为共享任务，在其他任务流中是非上线状态，所以不能上线", taskId));
                            break;
                        }

                        /**
                         * 无权限的
                         */
                        if(!hasTaskExecuteAuth(user, taskId)){
                           canOnline = false;
                           results.add(String.format("没有任务%d的权限", taskId));
                           break;
                        }

                        canOnlineTaskIds.add(taskId);
                    } catch (Throwable e) {
                        canOnline = false;
                        LOG.error("online error:" + taskId, e);
                        results.add(String.format("任务%上线异常:" + e.getMessage(), taskId));
                    }
                }
            }
            /**
             * 可以上线时，直接上线
             */
            if (canOnline && CollectionUtils.isNotEmpty(canOnlineTaskIds)) {
                for (List<Long> levelTaskIds : dagTaskIdLevel) {
                    for (Long taskId : levelTaskIds) {
                        if (!aleadyOnlineId.contains(taskId)) {
                            taskService.online(taskId);
                        }
                    }
                }
            }
        }

        /**
         * 任务流状态上线
         */
        if (canOnline && flow.getStatus() != FlowStatus.ONLINE.getValue()) {
            Flow flowUpdate = new Flow();
            flowUpdate.setId(flowId);
            flowUpdate.setStatus(FlowStatus.ONLINE.getValue());
            this.update(flowUpdate);
        }

        return new Tuple<>(canOnline, results);
    }

    @Override
    @Transactional("consoleTxManager")
    public Tuple<Boolean, List<String>> offline(long flowId) {

        SessionUser user = SessionContext.getUser();

        Flow flow = flowMapper.getById(flowId);

        List<String> results = Lists.newArrayList();
        List<TaskDependency> dependencies = this.getDependencies(flowId);
        List<Long> taskIds = DagUtils.getTaskIds(dependencies);

        /**
         * 共享依赖和唯一依赖获取
         */
        Tuple<Set<Long>, Set<Long>> sharedAndUnique = getSharedAndUnique(flowId);
        //共享的依赖
        Set<Long> sharedDependency = sharedAndUnique.getA();
        //仅仅属于当前任务流的依赖
        Set<Long> flowUniqueDependency = sharedAndUnique.getB();

        List<List<Long>> dagTaskIdLevel = DagUtils.getTaskIdDagLevel(dependencies, taskIds);
        /**
         * 通过共享依赖获取部分共享任务
         */
        Set<Long> sharedTaskSet = getFlowSharedTask(flowId, taskIds, sharedDependency);

        List<Long> canOfflineTaskIds = Lists.newArrayList();
        //非上线状态任务
        Set<Long> unOlineTaskId = Sets.newHashSet();
        /**
         * 自下而上处理任务
         */
        if (CollectionUtils.isNotEmpty(dagTaskIdLevel)) {
            for (int i = dagTaskIdLevel.size() - 1; i >= 0; i--) {
                List<Long> levelTaskIds = dagTaskIdLevel.get(i);
                for (Long taskId : levelTaskIds) {
                    try{
                        Task task = taskService.getById(taskId);
                        /**
                         * 非上线状态的不处理,忽略
                         */
                        if(task.getStatus() != TaskStatus.ONLINE.getValue()){
                            unOlineTaskId.add(taskId);
                            continue;
                        }
                        /**
                         * 共享任务，忽略
                         */
                        if(sharedTaskSet.contains(taskId)){
                            results.add(String.format("任务:%s(%d)为共享任务，不能直接下线", task.getName(), task.getId()));
                            continue;
                        }
                        /**
                         * 权限校验
                         */
                        if(!hasTaskExecuteAuth(user, taskId)){
                            results.add(String.format("无任务:%s(%d)权限，不能直接下线", task.getName(), task.getId()));
                            continue;
                        }
                        /**
                         * 校验是否可以下线
                         */
                        Tuple<Boolean, List<TaskDependency>> tryOfflineResult = taskService.tryOffline(taskId);
                        if(tryOfflineResult.getA()){
                            canOfflineTaskIds.add(taskId);
                        }
                    }catch (Throwable e){
                        LOG.error("task offline error:" + taskId, e);
                        results.add(e.getMessage());
                    }
                }
            }

        }

        /**
         * 如果依赖只有当前任务流使用，直接删除
         */
        if(CollectionUtils.isNotEmpty(flowUniqueDependency)){
            flowUniqueDependency.forEach(dependencyId -> {
                dependencyService.delete(dependencyId);
            });
        }

        /**
         * 去除任务流与依赖之间的关联
         */
        flowDependencyService.deleteByFlowId(flowId);
        /**
         * 删除原有快照，重新建立
         */
        snapshotService.clearAndAdd(flowId, dependencies);
        /**
         * 任务流下线
         */
        if (flow.getStatus() != FlowStatus.OFFLINE.getValue()) {
            Flow flowUpdate = new Flow();
            flowUpdate.setId(flowId);
            flowUpdate.setStatus(FlowStatus.OFFLINE.getValue());
            this.update(flowUpdate);
        }

        /**
         * 下线可以下线的任务
         */
        if (CollectionUtils.isNotEmpty(canOfflineTaskIds)) {
            for (Long taskId : canOfflineTaskIds) {
                taskService.offline(taskId);
            }
        }

        return new Tuple<>(true, results);
    }

    /**
     * 获取共享任务id
     *
     * @param taskIds
     * @param sharedDependency
     */
    private Set<Long> getFlowSharedTask(Long flowId, List<Long> taskIds, Set<Long> sharedDependency) {
        Set<Long> sharedTaskSet = Sets.newHashSet();
        /**
         * 1.通过共享依赖获取部分共享任务
         * 2.通过检查人是否除了当前集合以外还有没有其它依赖
         */
        if (CollectionUtils.isNotEmpty(sharedDependency)) {
            for (Long dependencyId : sharedDependency) {
                TaskDependency dependency = dependencyService.getById(dependencyId);
                //通过共享的依赖取出共享的任务
                sharedTaskSet.add(dependency.getTaskId());
                sharedTaskSet.add(dependency.getUpstreamTaskId());
            }
        }
        //判断共享依赖以外的任务是否还有共享任务
        if (CollectionUtils.isNotEmpty(taskIds)) {
            //从任务下游开始下线task
            for (Long taskId : taskIds) {
                if (sharedTaskSet.contains(taskId)) {
                    continue;
                }
                Set<Long> flowIdSet = getOnlineFlowIdSet(taskId);
                /**
                 * 1.所属集合大于1个
                 * 2.等于一个，且集合id不属于当前集合
                 */
                if (CollectionUtils.isNotEmpty(flowIdSet)) {
                    if (flowIdSet.size() > 1) {
                        sharedTaskSet.add(taskId);
                    } else if (flowIdSet.size() == 1 && !flowIdSet.contains(flowId)) {
                        sharedTaskSet.add(taskId);
                    }
                }

            }
        }
        return sharedTaskSet;
    }

    /**
     * 获取任务所属任务流Id
     * @param taskId
     * @return
     */
    @Override
    public Set<Long> getOnlineFlowIdSet(Long taskId) {
        Set<Long> flowIdSet = Sets.newHashSet();

        List<Long> taskDependencyIds = Lists.newArrayList();
        /**
         * 获取上游
         */
        List<TaskDependency> dependencyByTaskId = dependencyService.getUpstreamDependencies(taskId);
        if (CollectionUtils.isNotEmpty(dependencyByTaskId)) {
            for (TaskDependency td : dependencyByTaskId) {
                taskDependencyIds.add(td.getId());
            }
        }
        /**
         * 获取下游
         */
        List<TaskDependency> upstreams = dependencyService.getDownstreamDependencies(taskId);
        if (CollectionUtils.isNotEmpty(upstreams)) {
            for (TaskDependency td : upstreams) {
                taskDependencyIds.add(td.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(taskDependencyIds)) {
            List<FlowDependency> flowDependencies = flowDependencyService.getByDependencyIds(taskDependencyIds);
            /**
             * 如果集合id多余两个即为共享任务
             */
            if (CollectionUtils.isNotEmpty(flowDependencies)) {
                for (FlowDependency cd : flowDependencies) {
                    flowIdSet.add(cd.getFlowId());
                }

            }

        }
        return flowIdSet;
    }

    @Override
    public Set<Long> getUnOnlineFlowIdSet(Long taskId) {
        Set<Long> flowIdSet = Sets.newHashSet();

        List<FlowDependencySnapshot> downstreamDependencySnapshots = snapshotService.getDownstreamDependencySnapshots(taskId);
        if(CollectionUtils.isNotEmpty(downstreamDependencySnapshots)){
            downstreamDependencySnapshots.stream().map(FlowDependencySnapshot::getFlowId).forEach(flowId -> flowIdSet.add(flowId));
        }

        List<FlowDependencySnapshot> upstreamDependencySnapshots = snapshotService.getUpstreamDependencySnapshots(taskId);
        if(CollectionUtils.isNotEmpty(upstreamDependencySnapshots)){
            upstreamDependencySnapshots.stream().map(FlowDependencySnapshot::getFlowId).forEach(flowId -> flowIdSet.add(flowId));
        }


        return flowIdSet;
    }

    @Override
    public int statisByStatus(Integer status) {
        return Optional.ofNullable(flowMapper.statisByStatus(status)).orElse(0);
    }

    @Override
    @Transactional("consoleTxManager")
    public void addOrUpdateDependencies(long flowId, List<TaskDependency> dependencies) {
        //任务流原来的依赖关系
        List<FlowDependency> flowOriginalDependencies = flowDependencyService.getFlowDependencies(flowId);
        //任务任务流已经存在的依赖id
        Set<Long> flowDependencyIdSet = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(flowOriginalDependencies)) {
            for (FlowDependency fd : flowOriginalDependencies) {
                flowDependencyIdSet.add(fd.getTaskDependencyId());
            }
        }
        //任务流所有的依赖关系
        List<FlowDependency> flowDependencies = Lists.newArrayList();
        //用来记录需要删除的链接
        List<Long> toDeleteDependencies = Lists.newArrayList();
        //记录新提交的依赖在任务任务流中已经存在的id
        Set<Long> alreadyExitFlowDepenIdSet = Sets.newHashSet();
        //用来记录新添加得连接
        List<TaskDependency> newDependencies = Lists.newArrayList();
        //用来记录需要更新的
        List<TaskDependency> updateDependencies = Lists.newArrayList();

        /**
         * 验证依赖是否已经存在
         */
        for (TaskDependency dependency : dependencies) {
            TaskDependency td = dependencyService.get(dependency.getTaskId(), dependency.getUpstreamTaskId());
            //如果存在依赖
            if (td != null) {
                boolean dependencyHasChanged = false;
                long dependencyFirstFlow = flowDependencyService.getDependencyFirstFlow(td.getId());
                /**
                 *  如果依赖已经存在，但是不属于这个任务任务流
                 */
                if (!flowDependencyIdSet.contains(td.getId())) {
                    //如果依赖状态为disabled
                    if (td.getStatus() == StatusType.OFF.getValue()) {
                        td.setStatus(StatusType.ON.getValue());
                        dependencyHasChanged = true;
                    }
                    //如果已经存在，查看config是否有更新
                    if (td.getType() != dependency.getType()
                            || StringUtils.equals(td.getConfig(), dependency.getConfig())) {
                        //config有更新并且当前任务流是第一个把这个依赖添加进来的
                        if (dependencyFirstFlow == 0 || dependencyFirstFlow == flowId) {
                            td.setType(dependency.getType());
                            td.setConfig(dependency.getConfig());
                            dependencyHasChanged = true;
                        } else {
                            throw new CommonRuntimeException(String.format("依赖%d-%d是共享任务流依赖,只有第一个添加依赖的任务流(id:%d)可以修改",
                                    dependency.getTaskId(), dependency.getUpstreamTaskId(), dependencyFirstFlow));
                        }
                    }
                    if (dependencyHasChanged) {
                        updateDependencies.add(td);
                    }
                    /**
                     * 虽然当前依赖已经添加到另外一个任务流，
                     * 当前任务流可以添加一个与依赖之间的关联，即多个任务流共享一个依赖
                     */
                    FlowDependency flowDependency = new FlowDependency();
                    flowDependency.setFlowId(flowId);
                    flowDependency.setTaskDependencyId(td.getId());
                    flowDependencies.add(flowDependency);
                } else {
                    //任务流已经存在的依赖id
                    alreadyExitFlowDepenIdSet.add(td.getId());
                    //如果已经存在，查看config是否有更新,设置config来更新
                    if (td.getType() != dependency.getType()
                            || StringUtils.equals(td.getConfig(), dependency.getConfig())) {
                        if (dependencyFirstFlow == 0 || dependencyFirstFlow == flowId) {
                            td.setType(dependency.getType());
                            td.setConfig(dependency.getConfig());
                            updateDependencies.add(td);
                        } else {
                            throw new CommonRuntimeException(String.format("依赖%d-%d是共享任务流依赖,只有第一个添加依赖的任务流(id:%d)可以修改",
                                    dependency.getTaskId(), dependency.getUpstreamTaskId(), dependencyFirstFlow));
                        }
                    }
                }
            } else {
                //依赖不存在，需要添加
                newDependencies.add(dependency);
            }
        }
        /**
         * 更新config
         */
        if (CollectionUtils.isNotEmpty(updateDependencies)) {
            for (TaskDependency dependency : updateDependencies) {
                dependencyService.update(dependency);
            }
        }
        /**
         * 计算哪些依赖是需要删除的
         */
        if (CollectionUtils.isNotEmpty(flowDependencyIdSet)) {
            for (Long id : flowDependencyIdSet) {
                if (!alreadyExitFlowDepenIdSet.contains(id)) {
                    toDeleteDependencies.add(id);
                }
            }
        }
        /**
         * 删除不再使用的依赖
         */
        if (CollectionUtils.isNotEmpty(toDeleteDependencies)) {
            for (Long dependencyId : toDeleteDependencies) {
                flowDependencyService.delete(dependencyId, flowId);
            }
        }
        /**
         * 添加任务流与依赖直接的关联
         */
        if (CollectionUtils.isNotEmpty(newDependencies)) {
            for (TaskDependency dependency : newDependencies) {
                dependencyService.add(dependency);
                FlowDependency flowDependency = new FlowDependency();
                flowDependency.setFlowId(flowId);
                flowDependency.setTaskDependencyId(dependency.getId());
                flowDependencies.add(flowDependency);
            }
        }
        /**
         * 建立任务流与依赖直接的联系
         */
        if (CollectionUtils.isNotEmpty(flowDependencies)) {
            flowDependencyService.addBatch(flowDependencies);
        }

    }

    @Override
    public Set<Long> getFlowIdSetByTask(long taskId) {

        /**
         * 获取上线的任务流
         */
        Set<Long> flowIdSet = Sets.newHashSet();
        Set<Long> taskFlowIdSet = this.getOnlineFlowIdSet(taskId);
        if(CollectionUtils.isNotEmpty(taskFlowIdSet)){
            flowIdSet.addAll(taskFlowIdSet);
        }

        /**
         * 获取任务所属的未上线的任务流
         */
        Set<Long> unOnlineFlowIdSet = this.getUnOnlineFlowIdSet(taskId);
        if(CollectionUtils.isNotEmpty(unOnlineFlowIdSet)){
            flowIdSet.addAll(unOnlineFlowIdSet);
        }

        return flowIdSet;
    }
}
