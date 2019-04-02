package cn.lite.flow.executor.kernel.schedule;

import cn.lite.flow.common.job.basic.AbstractUnstatefullJob;
import cn.lite.flow.executor.common.utils.ContainerMetadata;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.AbstractContainer;
import cn.lite.flow.executor.model.kernel.Container;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @description: 容器补偿，类似服务重启后，任务需要重新放到container中
 * @author: yueyunyue
 * @create: 2019-01-17
 **/
public class ContainerCompensateJob extends AbstractUnstatefullJob {

    @Override
    public void executeInternal() {
        this.removeFinishJob();
    }


    /**
     * 处理已经完成的任务
     */
    private void removeFinishJob(){
        List<Container> containers = ContainerMetadata.getAllContainers();
        if(CollectionUtils.isNotEmpty(containers)){
            containers.stream().forEach(container -> {
                AbstractContainer abstractContainer = (AbstractContainer)container;
                if(abstractContainer.isFailed() || abstractContainer.isSuccess()){
                    ExecutorJob executorJob = abstractContainer.getExecutorJob();
                    if(executorJob != null){
                        Long executorJobId = executorJob.getId();
                        ContainerMetadata.removeContainer(executorJobId);
                        LOG.info("remove container,id:{}", executorJob);
                    }else{
                        LOG.error("container has no executorJob, contianer:" + container);
                    }
                }
            });
        }
    }


}
