package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.dao.mapper.FlowDependencyMapper;
import cn.lite.flow.console.model.basic.FlowDependency;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.FlowDependencyQM;
import cn.lite.flow.console.service.FlowDependencyService;
import cn.lite.flow.console.service.TaskDependencyService;
import cn.lite.flow.console.service.TaskService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ly on 2018/7/23.
 */
@Service
public class FlowDependencyServiceImpl implements FlowDependencyService {

    @Autowired
    private FlowDependencyMapper flowDependencyMapper;

    @Autowired
    private TaskDependencyService taskDependencyService;

    @Autowired
    private TaskService taskService;

    @Override
    public void add(FlowDependency model) {
        flowDependencyMapper.insert(model);
    }

    @Override
    public FlowDependency getById(long id) {
        return flowDependencyMapper.getById(id);
    }

    @Override
    public int update(FlowDependency model) {
        return flowDependencyMapper.update(model);
    }

    @Override
    public int count(FlowDependencyQM queryModel) {
        return flowDependencyMapper.count(queryModel);
    }

    @Override
    public List<FlowDependency> list(FlowDependencyQM queryModel) {
        return flowDependencyMapper.findList(queryModel);
    }

    @Override
    public void addBatch(List<FlowDependency> flowDependencies) {
        flowDependencyMapper.insertBatch(flowDependencies);
    }

    @Override
    public List<TaskDependency> getDependencies(long flowId) {
        FlowDependencyQM qm = new FlowDependencyQM();
        qm.setFlowId(flowId);
        List<FlowDependency> flowDependencies = flowDependencyMapper.findList(qm);
        if(CollectionUtils.isNotEmpty(flowDependencies)){
            List<TaskDependency> taskDependencies = Lists.newArrayList();
            for(FlowDependency flowDependency : flowDependencies){
                TaskDependency taskDependency = taskDependencyService.getById(flowDependency.getTaskDependencyId());
                taskDependencies.add(taskDependency);
            }
            return taskDependencies;

        }
        return null;
    }

    @Override
    public List<FlowDependency> getFlowDependencies(long flowId) {
        FlowDependencyQM qm = new FlowDependencyQM();
        qm.setFlowId(flowId);
        List<FlowDependency> flowDependencies = flowDependencyMapper.findList(qm);
        return flowDependencies;
    }

    @Override
    public long getDependencyFirstFlow(long denpendencyId) {
        FlowDependencyQM qm = new FlowDependencyQM();
        qm.setTaskDependencyId(denpendencyId);
        qm.addOrderAsc(FlowDependencyQM.COL_ID);
        qm.setPage(Page.getPage(0, 1));
        List<FlowDependency> list = flowDependencyMapper.findList(qm);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0).getFlowId();
        }
        return 0;
    }

    @Override
    public boolean isFlowUnique(long dependencyId, long flowId) {
        FlowDependencyQM qm = new FlowDependencyQM();
        qm.setFlowId(flowId);
        qm.setTaskDependencyId(dependencyId);
        List<FlowDependency> flowDependencies = flowDependencyMapper.findList(qm);
        if(CollectionUtils.isNotEmpty(flowDependencies)
                && flowDependencies.size() == 1
                && flowDependencies.get(0).getFlowId() == flowId){
            return true;
        }
        return false;
    }

    @Override
    public void delete(long dependencyId, long flowId) {
        flowDependencyMapper.delete(dependencyId, flowId);
    }

    @Override
    public void deleteByFlowId(long flowId) {
        flowDependencyMapper.deleteByFlowId(flowId);
    }

    @Override
    public List<FlowDependency> getByDependencyId(long dependencyId) {

        FlowDependencyQM qm = new FlowDependencyQM();
        qm.setTaskDependencyId(dependencyId);
        List<FlowDependency> flowDependencies = flowDependencyMapper.findList(qm);
        return flowDependencies;
    }

    @Override
    public List<FlowDependency> getByDependencyIds(List<Long> dependencyIds) {
        if(CollectionUtils.isEmpty(dependencyIds)){
            return null;
        }
        FlowDependencyQM qm = new FlowDependencyQM();
        qm.setTaskDependencyIds(dependencyIds);
        List<FlowDependency> flowDependencies = flowDependencyMapper.findList(qm);
        return flowDependencies;
    }

}
