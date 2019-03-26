package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.FlowDependency;
import cn.lite.flow.console.model.query.FlowDependencyQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface FlowDependencyMapper extends BaseMapper<FlowDependency, FlowDependencyQM> {


    /**
     * 批量添加
     * @param dependencies
     */
    void insertBatch(@Param("dependencies") List<FlowDependency> dependencies);

    /**
     * 删除任务流与依赖的关联
     * @param dependencyId
     * @param flowId
     * @return
     */
    void delete(@Param("dependencyId") long dependencyId, @Param("flowId") long flowId);

    /**
     * 删除任务流与任务依赖之间的管理
     * @param flowId
     */
    void deleteByFlowId(long flowId);
}
