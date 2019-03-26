package cn.lite.flow.executor.client;

import cn.lite.flow.executor.client.model.JobParam;
import cn.lite.flow.executor.client.model.SubmitExecuteJob;
import cn.lite.flow.executor.model.basic.ExecutorJob;

import java.util.List;

/**
 * Created by ly on 2018/11/19.
 */
public interface ExecutorJobRpcService {
    /**
     * 提交任务
     * @param submitExecuteJob
     * @return
     */
    Long submitJob(SubmitExecuteJob submitExecuteJob);

    /**
     * 根据id获取执行任务
     * @param id
     * @return
     */
    ExecutorJob getById(long id);

    /**
     * 根据名称模糊查询
     *
     * @return  返回10条
     */
    List<ExecutorJob> list(JobParam jobParam);

    /**
     * 获取数量
     * @param jobParam
     * @return
     */
    int count(JobParam jobParam);

    /**
     * kill 任务
     * @param id
     * @return
     */
    void kill(long id);

    /**
     * kill 任务
     * @param id
     * @param isCallback
     */
    void kill(long id, boolean isCallback);
    /**
     *
     * @param id
     */
    void callback(long id);

    /**
     * 获取日志
     * @param id
     * @return
     */
    String getLog(long id);

}
