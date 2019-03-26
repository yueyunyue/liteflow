package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.FlowDependencySnapshot;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.FlowDependencySnapshotQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface FlowDependencySnapshotService extends BaseService<FlowDependencySnapshot, FlowDependencySnapshotQM> {

    /**
     * 批量添加
     * @param snapshots
     */
    void addBatchSnapshots(List<FlowDependencySnapshot> snapshots);

    /**
     * 批量添加
     * @param flowId
     * @param dependencies
     */
    void addBatchDependencies(long flowId, List<TaskDependency> dependencies);

    /**
     * 从快照中获取任务流依赖
     * @param flowId
     * @return
     */
    List<TaskDependency> getDependencies(long flowId);

    /**
     * 删除快照
     * @param flowId
     */
    void deleteFlowSnapshot(long flowId);

    /**
     * 删除原有快照，然后新建
     * @param flowId
     * @param dependencies
     */
    void clearAndAdd(long flowId, List<TaskDependency> dependencies);

    /**
     * 任务的上游依赖
     * @param taskId
     * @return
     */
    List<TaskDependency> getUpstreamDependencies(long taskId);

    /**
     * 获取上游依赖快照
     * @param taskId
     * @return
     */
    List<FlowDependencySnapshot> getUpstreamDependencySnapshots(long taskId);

    /**
     * 任务的下游
     * @param taskId
     * @return
     */
    List<TaskDependency> getDownstreamDependencies(long taskId);

    /**
     * 获取下游依赖快照
     * @param taskId
     * @return
     */
    List<FlowDependencySnapshot> getDownstreamDependencySnapshots(long taskId);

    /**
     * 获取上游任务id
     * @param taskId
     * @return
     */
    List<Long> getUpstreamTaskIds(long taskId);

    /**
     * 获取下游任务id
     * @param taskId
     * @return
     */
    List<Long> getDownstreamTaskIds(long taskId);

}
