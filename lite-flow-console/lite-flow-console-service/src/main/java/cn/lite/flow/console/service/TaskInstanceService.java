package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.query.TaskInstanceQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskInstanceService extends BaseService<TaskInstance, TaskInstanceQM> {

    /**
     * 批量获取实例
     * @param instanceIds
     * @return
     */
    List<TaskInstance> getByIds(List<Long> instanceIds);

    /**
     * kill掉实例
     * @param instanceId
     * @return
     */
    boolean kill(long instanceId);

    /**
     * kill掉任务，
     * @param instanceId
     * @param isExecutorCallback   执行者是否回调
     * @return
     */
    boolean kill(long instanceId, boolean isExecutorCallback);

    /**
     * 更新，通过status做乐观锁
     * @param instance
     * @param status
     * @return
     */
    int updateWithStatus(TaskInstance instance, int status);

    /**
     * 更新信息
     * @param msg
     * @param id
     * @return
     */
    int updateMsg(long id, String msg);


    /**
     * 获取ready的实例
     * @return
     */
    List<Long> listReady2RunInstance();

    /**
     * 获取准备提交的任务
     * @return
     */
    List<Long> listReady2SubmitInstance();

    /**
     * 获取已经提交的实例
     * @return
     */
    List<Long> listSubmitedInstance();

    /**
     * 获取任务的相关版本的最新实例
     *
     * @param taskId            任务id
     * @param taskVersionNo     任务版本
     * @return
     */
    TaskInstance getLatestInstance(Long taskId, Long taskVersionNo);

    /**
     * 统计某时间段内处于某种状态的实例数量
     *
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param status            实例状态
     * @return
     */
    int statis(String startTime, String endTime, Integer status);

    /**
     * 获取日志
     * @param instanceId
     * @return
     */
    String getLog(long instanceId);

}
