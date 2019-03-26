package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.dao.mapper.TaskInstanceDependencyMapper;
import cn.lite.flow.console.model.basic.TaskInstanceDependency;
import cn.lite.flow.console.model.query.TaskInstanceDependencyQM;
import cn.lite.flow.console.service.TaskInstanceDependencyService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class TaskInstanceDependencyServiceImpl implements TaskInstanceDependencyService {

    private final static Logger LOG = LoggerFactory.getLogger(TaskInstanceDependencyServiceImpl.class);

    @Autowired
    private TaskInstanceDependencyMapper taskInstanceDependencyMapper;

    @Override
    public void add(TaskInstanceDependency model) {
        taskInstanceDependencyMapper.insert(model);
    }

    @Override
    public TaskInstanceDependency getById(long id) {
        return taskInstanceDependencyMapper.getById(id);
    }

    @Override
    public int update(TaskInstanceDependency model) {
        return taskInstanceDependencyMapper.update(model);
    }

    @Override
    public int count(TaskInstanceDependencyQM queryModel) {
        return taskInstanceDependencyMapper.count(queryModel);
    }

    @Override
    public List<TaskInstanceDependency> list(TaskInstanceDependencyQM queryModel) {
        return taskInstanceDependencyMapper.findList(queryModel);
    }

    @Override
    public void addBatch(List<TaskInstanceDependency> instanceDependencies) {
        if(CollectionUtils.isNotEmpty(instanceDependencies)){
            taskInstanceDependencyMapper.insertBatch(instanceDependencies);
        }else{
            LOG.error("instance dependencies is empty");
        }

    }

    @Override
    public List<TaskInstanceDependency> listValidInstanceDependency(long instanceId) {
        TaskInstanceDependencyQM qm = new TaskInstanceDependencyQM();
        qm.setInstanceId(instanceId);
        qm.setStatus(StatusType.ON.getValue());
        return taskInstanceDependencyMapper.findList(qm);
    }

    @Override
    public List<TaskInstanceDependency> listVersionValidDependency(long upstreamTaskId, long upstreamTaskVersionNo) {
        TaskInstanceDependencyQM qm = new TaskInstanceDependencyQM();
        qm.setUpstreamTaskId(upstreamTaskId);
        qm.setUpstreamTaskVersionNo(upstreamTaskVersionNo);
        qm.setStatus(StatusType.ON.getValue());
        return taskInstanceDependencyMapper.findList(qm);
    }
}
