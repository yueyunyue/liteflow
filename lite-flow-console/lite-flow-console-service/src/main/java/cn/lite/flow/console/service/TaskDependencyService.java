package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.TaskDependencyQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskDependencyService extends BaseService<TaskDependency, TaskDependencyQM> {

    /**
     * 任务的上游依赖
     * @param taskId
     * @return
     */
    List<TaskDependency> getUpstreamDependencies(long taskId);

    /**
     * 获取上游任务id
     * @param taskId
     * @return
     */
    List<Long> getUpstreamTaskIds(long taskId);

    /**
     * 任务的下游
     * @param taskId
     * @return
     */
    List<TaskDependency> getDownstreamDependencies(long taskId);

    /**
     * 获取下游任务id
     * @param taskId
     * @return
     */
    List<Long> getDownstreamTaskIds(long taskId);

    /**
     * 获取
     * @param taskId
     * @param upstreamTaskId
     * @return
     */
    TaskDependency get(long taskId, long upstreamTaskId);
    /**
     * 依赖置为无效
     * @param dependencyId
     * @return
     */
//    int invalidDependency(long dependencyId);

    /**
     * 依赖设置为有效
     * @param dependencyId
     * @return
     */
    int validDependency(long dependencyId);

    /**
     * 删除依赖
     * @param dependencyId
     */
    void delete(long dependencyId);

}
