package cn.lite.flow.console.service;

import cn.lite.flow.console.model.basic.Task;

import java.util.List;

/**
 * @description: 报警服务
 * @author: yueyunyue
 * @create: 2019-03-11
 **/
public interface AlarmService {

    /**
     * 任务报警，包括电话、邮件
     * @param taskId
     * @param msg
     */
    void alarmTask(long taskId, String msg);
    /**
     * 任务报警，包括电话、邮件
     * @param task
     * @param msg
     */
    void alarmTask(Task task, String msg);

    /**
     * 邮件报警
     * @param
     * @param msg
     */
    void alarmEmail(List<String> emails, String msg);

    /**
     * 电话报警
     * @param phones
     * @param msg
     */
    void alarmPhone(List<String> phones, String msg);

}
