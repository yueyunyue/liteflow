package cn.lite.flow.console.kernel.event.handler;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.console.kernel.event.handler.check.Checker;
import cn.lite.flow.console.model.event.consts.Event;
import cn.lite.flow.console.model.event.model.ScheduleEvent;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.consts.TaskVersionStatus;
import cn.lite.flow.console.service.TaskInstanceService;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.service.TaskVersionService;
import cn.lite.flow.console.service.queue.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @description: 任务是否符合ready状态
 * @author: yueyunyue
 * @create: 2018-07-30
 **/
public class ReadyEventHandler implements EventHandler{

    private final static Logger LOG = LoggerFactory.getLogger(ReadyEventHandler.class);

    @Autowired
    private TaskInstanceService instanceService;

    @Autowired
    private TaskVersionService versionService;

    private List<Checker<TaskInstance>> checkers;

    public ReadyEventHandler(List<Checker<TaskInstance>> checkers) {
        this.checkers = checkers;
    }

    @Override
    @Transactional("consoleTxManager")
    public boolean handle(ScheduleEvent event) {
        Long instanceId = event.getEventTargetId();
        TaskInstance taskInstance = instanceService.getById(instanceId);
        if(taskInstance.getStatus() != TaskVersionStatus.INIT.getValue()){
            LOG.info("instance({}) status is not init", instanceId);
            return false;
        }
        String msg = "等待提交";

        boolean isPassed = true;
        for(Checker<TaskInstance> checker : checkers){
            Tuple<Boolean, String> checkResult = checker.check(event, taskInstance);
            /**
             * 验证没有通过，更新消息然后退出
             */
            if(!checkResult.getA()){
                msg = checkResult.getB();
                isPassed = false;
                break;
            }
        }
        /**
         * 更新实例和对应的任务版本，状态变为ready
         */
        if(isPassed){
            /**
             * 更新实例
             */
            int readyStatus = TaskVersionStatus.READY.getValue();
            TaskInstance instanceUpdate = new TaskInstance();
            instanceUpdate.setId(instanceId);
            instanceUpdate.setMsg(msg);
            instanceUpdate.setStatus(readyStatus);
            instanceService.updateWithStatus(instanceUpdate, TaskVersionStatus.INIT.getValue());

            /**
             * 更新任务版本
             */
            TaskVersion versionUpdate = new TaskVersion();
            versionUpdate.setId(taskInstance.getTaskVersionId());
            versionUpdate.setStatus(readyStatus);
            versionService.updateWithStatus(versionUpdate, TaskVersionStatus.INIT.getValue());

            //发送提交事件
            ScheduleEvent readyEvent = ScheduleEvent.newInstance(Event.SUBMIT, instanceId);
            EventQueue.put(readyEvent);
        }else{
            //失败:更新消息
            TaskInstance instanceUpdate = new TaskInstance();
            instanceUpdate.setId(instanceId);
            instanceUpdate.setMsg(msg);
            instanceService.updateWithStatus(instanceUpdate, TaskVersionStatus.INIT.getValue());

        }
        return true;
    }
}
