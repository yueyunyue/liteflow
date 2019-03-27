package cn.lite.flow.executor.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.executor.model.basic.ExecutorCallback;
import cn.lite.flow.executor.model.query.ExecutorCallbackQM;

import java.util.List;

/**
 * @description: 回调
 * @author: yueyunyue
 * @create: 2018-09-13
 **/
public interface ExecutorCallbackService extends BaseService<ExecutorCallback, ExecutorCallbackQM> {

    /**
     * 回调
     * @param jobSourceId
     * @param jobStatus
     * @param msg
     * @return
     */
    boolean callback(long jobSourceId, int jobStatus, String msg);

    /**
     * callback回调
     * @param id
     * @return
     */
    boolean callbackById(long id);

    /**
     * 回调成功
     * @param id
     * @return
     */
    int callbackSuccess(long id);

    /**
     * 忽略
     * @param id
     */
    void ignore(long id);

    /**
     * 批量忽略
     * @param ids
     */
    void batchIgnore(List<Long> ids);

    /**
     * 停止job的callback
     * @param jobId
     */
    void ignoreCallbackStatusOfJob(long jobId);

}
