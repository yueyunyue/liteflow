package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.query.TaskInstanceQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskInstanceMapper extends BaseMapper<TaskInstance, TaskInstanceQM> {

    List<Long> findIdList(TaskInstanceQM queryModel);

    /**
     * 根据状态和taskInstance中指定的id更新记录
     *
     * @param taskInstance      要更新的记录
     * @param originStatus      更新前的状态
     * @return
     */
    int updateWithStatus(@Param("instance")TaskInstance taskInstance, @Param("originStatus") int originStatus);

    /**
     * 获取最新的任务相关版本实例
     *
     * @param taskId            任务id
     * @param taskVersionNo     任务版本
     * @return
     */
    TaskInstance getLatestInstance(@Param("taskId") Long taskId, @Param("taskVersionNo") Long taskVersionNo);

    /**
     * 获取版本最新实例
     * @param taskVersionId
     * @return
     */
    TaskInstance getVersionLatestInstance(@Param("taskVersionId") Long taskVersionId);

    /**
     * 统计一段时间内某状态下的实例数量
     *
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param status            实例状态    如果为null 则查询所有
     * @return
     */
    Integer statis(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("status") Integer status);

}
