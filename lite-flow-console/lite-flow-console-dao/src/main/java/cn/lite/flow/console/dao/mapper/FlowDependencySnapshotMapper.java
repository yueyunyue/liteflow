package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.FlowDependencySnapshot;
import cn.lite.flow.console.model.query.FlowDependencySnapshotQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface FlowDependencySnapshotMapper extends BaseMapper<FlowDependencySnapshot, FlowDependencySnapshotQM> {

    /**
     * 批量添加
     * @param dependencies
     */
    void insertBatch(@Param("dependencies") List<FlowDependencySnapshot> dependencies);
    /**
     * 删除任务流快照
     * @param flowId
     */
    void deleteByFlowId(long flowId);
}
