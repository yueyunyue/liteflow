package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.TaskInstanceDependency;
import cn.lite.flow.console.model.query.TaskInstanceDependencyQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskInstanceDependencyService extends BaseService<TaskInstanceDependency, TaskInstanceDependencyQM> {

    /**
     * 批量添加
     * @param instanceDependencies
     */
    void addBatch(List<TaskInstanceDependency> instanceDependencies);

    /**
     * 列举实例可用的依赖
     * @param instanceId
     * @return
     */
    List<TaskInstanceDependency> listValidInstanceDependency(long instanceId);

    /**
     * 获取依赖任务版本的实例依赖
     * @param upstreamTaskId
     * @param upstreamTaskVersionNo
     * @return
     */
    List<TaskInstanceDependency> listVersionValidDependency(long upstreamTaskId, long upstreamTaskVersionNo);




}
