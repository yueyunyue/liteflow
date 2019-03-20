package cn.lite.flow.console.kernel.job;

import cn.lite.flow.common.job.basic.AbstractUnstatefullJob;
import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.consts.TaskVersionStatus;
import cn.lite.flow.console.model.query.TaskInstanceQM;
import cn.lite.flow.console.service.AlarmService;
import cn.lite.flow.console.service.TaskInstanceService;
import cn.lite.flow.console.service.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @description: 实例运行超时报警
 * @author: yueyunyue
 * @create: 2019-03-20
 **/
public class InstanceRunOvertimeAlarmJob extends AbstractUnstatefullJob {

    @Autowired
    private TaskInstanceService instanceService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AlarmService alarmService;

    private final static int PAGE_SIZE = 100;

    private final static int MINUTE = 30;

    @Override
    public void executeInternal() {

        Date now = DateUtils.getNow();
        final int pageNo = 1;
        long baseId = 0;
        List<TaskInstance> instances = null;
        do {
            TaskInstanceQM instanceQM = new TaskInstanceQM();
            instanceQM.setStatus(TaskVersionStatus.RUNNING.getValue());
            instanceQM.setGreaterThanId(baseId);
            instanceQM.setPage(Page.getPageByPageNo(pageNo, PAGE_SIZE));
            instanceQM.addOrderAsc(TaskInstanceQM.COL_ID);

            instances = instanceService.list(instanceQM);
            if (CollectionUtils.isEmpty(instances)) {
                break;
            }
            instances.forEach(instance -> {
                Date logicRunTime = instance.getLogicRunTime();
                long betweenMinutes = DateUtils.betweenMinutes(logicRunTime, now);
                Task task = taskService.getById(instance.getTaskId());
                Integer maxRunTime = task.getMaxRunTime();

                if (maxRunTime != null
                        && maxRunTime > 0
                        && betweenMinutes > maxRunTime) {
                    String alarmMsg = String.format("任务版本:%d已运行%d分钟,超过最大运行时间%d",
                            instance.getTaskVersionNo(),
                            betweenMinutes,
                            maxRunTime);
                    alarmService.alarmTask(instance.getTaskId(), alarmMsg);
                }
            });
            baseId = instances.get(instances.size() - 1).getId();
        } while (CollectionUtils.isNotEmpty(instances));

    }
}
