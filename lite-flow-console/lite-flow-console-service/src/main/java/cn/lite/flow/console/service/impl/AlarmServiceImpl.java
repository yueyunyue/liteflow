package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.service.AlarmService;
import cn.lite.flow.console.service.TaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 报警
 * @author: yueyunyue
 * @create: 2019-03-11
 **/
@Service("alarmServiceImpl")
public class AlarmServiceImpl implements AlarmService {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Override
    public void alarmTask(long taskId, String msg) {

        Task task = taskService.getById(taskId);
        alarmTask(task, msg);


    }

    @Override
    public void alarmTask(Task task, String msg) {
        if(task == null){
            return;
        }

        String alarmPrefix = String.format("任务%s(id=%d)报警:",task.getName(), task.getId());
        msg = alarmPrefix + msg;

        String alarmEmail = task.getAlarmEmail();
        List<String> emails = alarmToString(alarmEmail);
        this.alarmEmail(emails, msg);

        String alarmPhone = task.getAlarmPhone();
        List<String> phones = alarmToString(alarmPhone);
        this.alarmPhone(phones, msg);
    }

    private List<String> alarmToString(String alarm){
        if(StringUtils.isBlank(alarm)){
            return null;
        }
        String[] alarmArray = StringUtils.split(alarm, CommonConstants.SEMICOLON);
        List<String> alarmList = Arrays.asList(alarmArray);
        return alarmList;

    }

    @Override
    public void alarmEmail(List<String> emails, String msg) {
        if(CollectionUtils.isEmpty(emails)){
            return;
        }
        LOG.info("alarm email:{},msg:{}", emails, msg);
    }

    @Override
    public void alarmPhone(List<String> phones, String msg) {
        if(CollectionUtils.isEmpty(phones)){
            return;
        }
        LOG.info("alarm phone:{},msg:{}", phones, msg);
    }
}
