package cn.lite.flow.console.kernel.job;

import cn.lite.flow.common.job.basic.AbstractUnstatefullJob;
import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.consts.TaskVersionStatus;
import cn.lite.flow.console.model.query.TaskInstanceQM;
import cn.lite.flow.console.service.AlarmService;
import cn.lite.flow.console.service.TaskInstanceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @description: 实例运行延迟报警
 * @author: yueyunyue
 * @create: 2019-03-18
 **/
public class InstanceRunDelayAlarmJob extends AbstractUnstatefullJob {

    @Autowired
    private TaskInstanceService instanceService;

    @Autowired
    private AlarmService alarmService;

    private final static int PAGE_SIZE = 100;

    private final static int MINUTE = 30;

    @Override
    public void executeInternal() {

        Date now = DateUtils.getNow();
        Date minutesAgo = DateUtils.minusMinute(new Date(), MINUTE);

        int pageNo = 1;
        List<TaskInstance> instances = null;
        do{
            TaskInstanceQM instanceQM = new TaskInstanceQM();
            instanceQM.setStatus(TaskVersionStatus.INIT.getValue());
            instanceQM.setPage(Page.getPageByPageNo(pageNo, PAGE_SIZE));
            instanceQM.setLogicRunTimeLessEqual(minutesAgo);
            instanceQM.addOrderAsc(TaskInstanceQM.COL_LOGIC_RUN_TIME);

            instances = instanceService.list(instanceQM);
            if(CollectionUtils.isEmpty(instances)){
                break;
            }
            instances.forEach(instance -> {
                Date logicRunTime = instance.getLogicRunTime();
                int betweenMinutes = DateUtils.betweenMinutes(logicRunTime, now);
                String alarmMsg = String.format("任务版本:%d执行延迟%d分钟,异常信息:%s",
                        instance.getTaskVersionNo(),
                        betweenMinutes,
                        instance.getMsg());

                alarmService.alarmTask(instance.getTaskId(), alarmMsg);
            });
            pageNo ++;
        }while (CollectionUtils.isNotEmpty(instances));

    }
}
