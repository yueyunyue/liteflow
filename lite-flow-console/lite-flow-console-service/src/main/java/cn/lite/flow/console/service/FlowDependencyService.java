package cn.lite.flow.console.service;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.FlowDependency;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.FlowDependencyQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface FlowDependencyService extends BaseService<FlowDependency, FlowDependencyQM> {

    /**
     * 批量添加
     * @param flowDependencies
     */
    void addBatch(List<FlowDependency> flowDependencies);

    /**
     * 获取任务流的依赖
     * @param flowId
     * @return
     */
    List<TaskDependency> getDependencies(long flowId);

    /**
     * 获取任务流与依赖的关系
     * @param flowId
     * @return
     */
    List<FlowDependency> getFlowDependencies(long flowId);

    /**
     * 获取与任务依赖建立关系的第一个任务流
     */
    long getDependencyFirstFlow(long denpendencyId);

    /**
     * 依赖是不是只属于当前任务流
     * @param dependencyId
     * @param flowId
     * @return
     */
    boolean isFlowUnique(long dependencyId, long flowId);

    /**
     * 删除任务流与依赖的关联
     * @param dependencyId
     * @param flowId
     * @return
     */
    void delete(long dependencyId, long flowId);

    /**
     * 删除任务流与任务依赖之间的管理
     * @param flowId
     */
    void deleteByFlowId(long flowId);

    /**
     * 通过依赖id，获取任务流与依赖的关系
     * @param dependencyId
     * @return
     */
    List<FlowDependency> getByDependencyId(long dependencyId);
    /**
     * 通过依赖id，获取任务流与依赖的关系
     * @param dependencyIds
     * @return
     */
    List<FlowDependency> getByDependencyIds(List<Long> dependencyIds);
}
