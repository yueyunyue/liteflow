package cn.lite.flow.executor.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.query.ExecutorJobQM;

/**
 * @description: 执行job
 * @author: yueyunyue
 * @create: 2018-09-13
 **/
public interface ExecutorJobService extends BaseService<ExecutorJob, ExecutorJobQM> {

    /**
     * 添加任务id
     * @param jobId
     * @param applicationId
     */
    void bindApplicationId(long jobId, String applicationId);

    /**
     * 添加应用id，并且状态改为运行中
     * @param jobId
     * @param applicationId
     */
    void bindApplicationIdAndRun(long jobId, String applicationId);

    /**
     * 设置成功
     * @param jobId
     */
    void success(long jobId);

    /**
     * 设置失败
     * @param jobId
     */
    void fail(long jobId, String errorMsg);

    /**
     * 回调任务
     * @param jobId
     * @return
     */
    boolean callback(long jobId);
    /**
     * 运行
     * @param jobId
     */
    void run(long jobId);

    /**
     * 杀死任务
     */
    void kill(long jobId);

    /**
     * kill 任务
     * @param jobId
     */
    void kill(long jobId, boolean isCallback);

    /**
     * 通过来源获取id
     * @param sourceId
     * @return
     */
    ExecutorJob getBySourceId(long sourceId);

}
