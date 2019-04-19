package cn.lite.flow.console.kernel.event.handler.check;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskInstanceDependency;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.consts.TaskVersionFinalStatus;
import cn.lite.flow.console.model.event.model.ScheduleEvent;
import cn.lite.flow.console.service.TaskInstanceDependencyService;
import cn.lite.flow.console.service.TaskVersionService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description: 实例依赖验证
 * @author: yueyunyue
 * @create: 2018-08-06
 **/
public class InstanceDependencyChecker implements Checker<TaskInstance> {

    private final static Logger LOG = LoggerFactory.getLogger(InstanceDependencyChecker.class);

    @Autowired
    private TaskInstanceDependencyService dependencyService;

    @Autowired
    private TaskVersionService versionService;

    @Override
    public Tuple<Boolean, String> check(ScheduleEvent event, TaskInstance taskInstance) {
        List<TaskInstanceDependency> instanceDependencies = dependencyService.listValidInstanceDependency(taskInstance.getId());
        if(CollectionUtils.isEmpty(instanceDependencies)){
            LOG.info("instance has no dependency " + taskInstance.getId());
            return new Tuple<>(true, "");
        }else{
            for(TaskInstanceDependency dependency : instanceDependencies){
                Long upstreamTaskId = dependency.getUpstreamTaskId();
                long upstreamTaskVersionNo = dependency.getUpstreamTaskVersionNo();
                TaskVersion taskVersion = versionService.getTaskVersion(upstreamTaskId, upstreamTaskVersionNo);
                if(taskVersion == null){
                    String msg = String.format("不存在任务版本(任务id:%d, 版本:%d)", taskVersion.getTaskId(), taskVersion.getVersionNo());
                    LOG.info(msg);
                    return new Tuple<>(false, msg);
                }
                if(taskVersion.getFinalStatus() != TaskVersionFinalStatus.SUCCESS.getValue()){
                    String msg = String.format("等待任务版本(任务id:%d, 版本:%d)执行完成", taskVersion.getTaskId(), taskVersion.getVersionNo());
                    return new Tuple<>(false, msg);
                }
            }
        }

        return new Tuple<>(true, "");
    }
}
