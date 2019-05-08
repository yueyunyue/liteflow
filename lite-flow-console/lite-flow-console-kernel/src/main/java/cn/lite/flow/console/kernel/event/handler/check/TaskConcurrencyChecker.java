package cn.lite.flow.console.kernel.event.handler.check;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.consts.BooleanType;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.consts.ExecuteStrategy;
import cn.lite.flow.console.model.consts.TaskVersionFinalStatus;
import cn.lite.flow.console.model.event.model.ScheduleEvent;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 任务并发验证
 * @author: yueyunyue
 * @create: 2018-08-06
 **/
public class TaskConcurrencyChecker implements Checker<TaskInstance> {

    private final static Logger LOG = LoggerFactory.getLogger(InstanceDependencyChecker.class);

    @Autowired
    private TaskVersionService versionService;

    @Autowired
    private TaskService taskService;

    @Override
    public Tuple<Boolean, String> check(ScheduleEvent event, TaskInstance taskInstance) {

        Long taskId = taskInstance.getTaskId();
        Long versionId = taskInstance.getTaskVersionId();
        Task task = taskService.getById(taskId);
        Integer executeStrategy = task.getExecuteStrategy();

        if(task.getIsConcurrency() != null && task.getIsConcurrency() == BooleanType.FALSE.getValue()){
            int taskVersions = versionService.getLessThanNoCount(taskId, TaskVersionFinalStatus.NEW.getValue(), taskInstance.getTaskVersionNo());
            if(taskVersions > 0){
                /**
                 * 等待其他版本执行结束
                 */
                if(executeStrategy == ExecuteStrategy.WAIT.getValue()){
                    String msg = "等待之前的任务版本执行完成";
                    LOG.info(msg);
                    return new Tuple<>(false, msg);
                /**
                 * 忽略当前版本
                 */
                }else if(executeStrategy == ExecuteStrategy.IGNORE.getValue()){
                    /**
                     * 要先kill掉，然后才能ignore，要走常规的状态流转
                     */
                    versionService.ignore(versionId);
                    String msg = "已有任务版本运行中，当前任务版本忽略";
                    LOG.info(msg);
                    return new Tuple<>(false, msg);
                }

            }
        }
        return new Tuple<>(true, "");
    }
}
