package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.query.TaskQM;
import org.apache.ibatis.annotations.Param;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskMapper extends BaseMapper<Task, TaskQM> {

    /**
     * 统计某状态下的任务数量
     *
     * @param status    任务状态  为null则统计总数
     * @return
     */
    Integer statisByStatus(@Param("status") Integer status);
}
