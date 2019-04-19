package cn.lite.flow.console.kernel.event.handler.check;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.event.model.ScheduleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
/**
 * @description: 实例依赖验证
 * @author: yueyunyue
 * @create: 2018-08-06
 **/
public class RunTimeChecker implements Checker<TaskInstance> {

    private final static Logger LOG = LoggerFactory.getLogger(RunTimeChecker.class);

    @Override
    public Tuple<Boolean, String> check(ScheduleEvent event, TaskInstance taskInstance) {

        Date now = DateUtils.getNow();
        Date logicRunTime = taskInstance.getLogicRunTime();
        if(logicRunTime.after(now)){
            LOG.error("logic run time is after current,instance:{}", taskInstance.toString());
            return new Tuple<>(false, "未到达执行时间");
        }

        return new Tuple<>(true, "");
    }
}
