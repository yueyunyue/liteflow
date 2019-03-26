package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.dao.mapper.TaskDependencyMapper;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.TaskDependencyQM;
import cn.lite.flow.console.service.TaskDependencyService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class TaskDependencyServiceImpl implements TaskDependencyService {

    @Autowired
    private TaskDependencyMapper taskDependencyMapper;

    @Override
    public void add(TaskDependency dependency) {
        dependency.setStatus(StatusType.ON.getValue());
        taskDependencyMapper.insert(dependency);
    }

    @Override
    public TaskDependency getById(long id) {
        return taskDependencyMapper.getById(id);
    }

    @Override
    public int update(TaskDependency dependency) {
       return taskDependencyMapper.update(dependency);
    }

    @Override
    public int count(TaskDependencyQM queryModel) {
        return taskDependencyMapper.count(queryModel);
    }

    @Override
    public List<TaskDependency> list(TaskDependencyQM queryModel) {
        return taskDependencyMapper.findList(queryModel);
    }

    @Override
    public List<TaskDependency> getUpstreamDependencies(long taskId) {
        TaskDependencyQM qm = new TaskDependencyQM();
        qm.setTaskId(taskId);
        return taskDependencyMapper.findList(qm);
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
    public List<TaskDependency> getDownstreamDependencies(long taskId) {
        TaskDependencyQM qm = new TaskDependencyQM();
        qm.setUpstreamTaskId(taskId);
        return taskDependencyMapper.findList(qm);
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

    @Override
    public TaskDependency get(long taskId, long upstreamTaskId) {
        TaskDependencyQM qm = new TaskDependencyQM();
        qm.setTaskId(taskId);
        qm.setUpstreamTaskId(upstreamTaskId);
        List<TaskDependency> list = taskDependencyMapper.findList(qm);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    @Override
    public int validDependency(long dependencyId) {
        TaskDependency taskDependency = new TaskDependency();
        taskDependency.setId(dependencyId);
        taskDependency.setStatus(StatusType.ON.getValue());
        return taskDependencyMapper.update(taskDependency);
    }

//    @Override
//    public int invalidDependency(long dependencyId) {
//        TaskDependency taskDependency = new TaskDependency();
//        taskDependency.setId(dependencyId);
//        taskDependency.setStatus(StatusType.OFF.getValue());
//        return taskDependencyMapper.update(taskDependency);
//    }

    @Override
    public void delete(long dependencyId) {
        taskDependencyMapper.delete(dependencyId);
    }

}
