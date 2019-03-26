package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.Flow;
import cn.lite.flow.console.model.query.FlowQM;
import org.apache.ibatis.annotations.Param;

/**
 * Created by ly on 2018/7/23.
 */
public interface FlowMapper extends BaseMapper<Flow, FlowQM> {

    /**
     * 统计特定状态的任务流总数
     *
     * @param status    任务流状态  如果为null, 则统计所有
     * @return
     */
    Integer statisByStatus(@Param("status") Integer status);
}
