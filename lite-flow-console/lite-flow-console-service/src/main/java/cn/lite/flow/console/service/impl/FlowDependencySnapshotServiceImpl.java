package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.FlowDependencySnapshotMapper;
import cn.lite.flow.console.model.basic.FlowDependencySnapshot;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.FlowDependencySnapshotQM;
import cn.lite.flow.console.service.FlowDependencySnapshotService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class FlowDependencySnapshotServiceImpl implements FlowDependencySnapshotService {

    @Autowired
    private FlowDependencySnapshotMapper flowDependencySnapshotMapper;

    @Override
    public void add(FlowDependencySnapshot model) {
        flowDependencySnapshotMapper.insert(model);
    }

    @Override
    public FlowDependencySnapshot getById(long id) {
        return flowDependencySnapshotMapper.getById(id);
    }

    @Override
    public int update(FlowDependencySnapshot model) {
        return flowDependencySnapshotMapper.update(model);
    }

    @Override
    public int count(FlowDependencySnapshotQM queryModel) {
        return flowDependencySnapshotMapper.count(queryModel);
    }

    @Override
    public List<FlowDependencySnapshot> list(FlowDependencySnapshotQM queryModel) {
        return flowDependencySnapshotMapper.findList(queryModel);
    }

    @Override
    public void addBatchSnapshots(List<FlowDependencySnapshot> snapshots) {
        if(CollectionUtils.isEmpty(snapshots)){
            return;
        }
        flowDependencySnapshotMapper.insertBatch(snapshots);
    }

    @Override
    public void addBatchDependencies(long flowId, List<TaskDependency> dependencies) {

        if(CollectionUtils.isEmpty(dependencies)){
            return;
        }
        List<FlowDependencySnapshot> snapshots = Lists.newArrayList();
        dependencies.forEach(dependency -> {
            FlowDependencySnapshot snapshot = FlowDependencySnapshot.taskDependency2snapshot(dependency);
            snapshot.setFlowId(flowId);
            snapshots.add(snapshot);
        });
        this.addBatchSnapshots(snapshots);
    }

    @Override
    public List<TaskDependency> getDependencies(long flowId) {
        FlowDependencySnapshotQM queryModel = new FlowDependencySnapshotQM();
        queryModel.setFlowId(flowId);
        List<FlowDependencySnapshot> snapshots = flowDependencySnapshotMapper.findList(queryModel);
        List<TaskDependency> dependencies = getTaskDependencies(snapshots);
        return dependencies;
    }

    @Override
    public void deleteFlowSnapshot(long flowId) {
        flowDependencySnapshotMapper.deleteByFlowId(flowId);
    }

    @Override
    @Transactional("consoleTxManager")
    public void clearAndAdd(long flowId, List<TaskDependency> dependencies) {
        flowDependencySnapshotMapper.deleteByFlowId(flowId);
        this.addBatchDependencies(flowId, dependencies);
    }

    @Override
    public List<TaskDependency> getUpstreamDependencies(long taskId) {
        List<FlowDependencySnapshot> snapshots = this.getUpstreamDependencySnapshots(taskId);
        List<TaskDependency> dependencies = getTaskDependencies(snapshots);
        return dependencies;
    }

    @Override
    public List<FlowDependencySnapshot> getUpstreamDependencySnapshots(long taskId) {
        FlowDependencySnapshotQM qm = new FlowDependencySnapshotQM();
        qm.setTaskId(taskId);
        List<FlowDependencySnapshot> snapshots = flowDependencySnapshotMapper.findList(qm);
        return snapshots;
    }

    private List<TaskDependency> getTaskDependencies(List<FlowDependencySnapshot> snapshots) {
        if(CollectionUtils.isNotEmpty(snapshots)){
            List<TaskDependency> dependencies = Lists.newArrayList();
            snapshots.forEach(snapshot -> {
                TaskDependency taskDependency = FlowDependencySnapshot.snapshot2TaskDependency(snapshot);
                dependencies.add(taskDependency);
            });
            return dependencies;
        }
        return null;
    }

    @Override
    public List<TaskDependency> getDownstreamDependencies(long taskId) {
        List<FlowDependencySnapshot> snapshots = this.getDownstreamDependencySnapshots(taskId);
        List<TaskDependency> dependencies = getTaskDependencies(snapshots);
        return dependencies;
    }

    @Override
    public List<FlowDependencySnapshot> getDownstreamDependencySnapshots(long taskId) {
        FlowDependencySnapshotQM qm = new FlowDependencySnapshotQM();
        qm.setUpstreamTaskId(taskId);
        List<FlowDependencySnapshot> snapshots = flowDependencySnapshotMapper.findList(qm);
        return snapshots;
    }

    @Override
    public List<Long> getUpstreamTaskIds(long taskId) {
        List<Long> taskIds = Lists.newArrayList();
        List<TaskDependency> upstreams = this.getUpstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(upstreams)){
            upstreams.stream().map(TaskDependency::getUpstreamTaskId).forEach(id -> taskIds.add(id));
        }
        return taskIds;
    }

    @Override
    public List<Long> getDownstreamTaskIds(long taskId) {
        List<Long> taskIds = Lists.newArrayList();
        List<TaskDependency> downstreams = this.getDownstreamDependencies(taskId);
        if(CollectionUtils.isNotEmpty(downstreams)){
            downstreams.stream().map(TaskDependency::getTaskId).forEach(id -> taskIds.add(id));
        }
        return taskIds;
    }
}
