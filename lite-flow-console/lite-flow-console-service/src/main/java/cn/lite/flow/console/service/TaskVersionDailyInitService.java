package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.TaskVersionDailyInit;
import cn.lite.flow.console.model.query.TaskVersionDailyInitQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskVersionDailyInitService extends BaseService<TaskVersionDailyInit, TaskVersionDailyInitQM> {

    /**
     * 批量添加
     * @param dailyInits
     */
    void batchAdd(List<TaskVersionDailyInit> dailyInits);

    /**
     * 获取任务某天的初始化
     * @param taskId
     * @param day
     * @return
     */
    TaskVersionDailyInit getTaskDailyInit(long taskId, long day);

    /**
     * 置为无效
     * @param id
     */
    void disableDailyInit(long id);

    /**
     * 置为有效
     * @param id
     */
    void enableDailyInit(long id);

    /**
     * 置为无效
     * @param taskId
     * @param day
     */
    void disableDailyInit(long taskId, long day);

    /**
     * 置为有效
     * @param taskId
     * @param day
     */
    void enableDailyInit(long taskId, long day);

    /**
     * 置为成功
     * @param id
     */
    void successDailyInit(long id);

    /**
     * 失败
     * @param id
     * @param msg
     */
    void failDailyInit(long id, String msg);


}
