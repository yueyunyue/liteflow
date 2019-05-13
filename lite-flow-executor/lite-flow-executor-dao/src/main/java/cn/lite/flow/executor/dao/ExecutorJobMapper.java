package cn.lite.flow.executor.dao;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.query.ExecutorJobQM;
import org.apache.ibatis.annotations.Param;

/**
 * 任务dao
 */
public interface ExecutorJobMapper extends BaseMapper<ExecutorJob, ExecutorJobQM> {

    /**
     * 绑定任务和应用id
     * @param jobId
     * @param applicationId
     */
    void bindApplicationId(@Param("jobId")long jobId, @Param("applicationId") String applicationId);

    /**
     * 通过sourceId 获取job
     * @param sourceId
     * @return
     */
    ExecutorJob getBySourceId(@Param("sourceId")long sourceId);


}