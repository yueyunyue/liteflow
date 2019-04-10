package cn.lite.flow.executor.kernel.schedule;

import cn.lite.flow.common.job.basic.AbstractUnstatefullJob;
import cn.lite.flow.common.utils.ExceptionUtils;
import cn.lite.flow.executor.common.utils.ContainerMetadata;
import cn.lite.flow.executor.common.utils.LiteThreadPool;
import cn.lite.flow.executor.model.kernel.AbstractContainer;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import cn.lite.flow.executor.model.kernel.Container;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @description: 容器fire
 * @author: yueyunyue
 * @create: 2019-01-17
 **/
public class ContainerFireJob extends AbstractUnstatefullJob {

    @Override
    public void executeInternal() {
        /**
         * 处理任务
         */
        List<Container> containers = ContainerMetadata.getAllContainers();
        runContainers(containers);

    }

    private void runContainers(List<Container> containers){
        if(CollectionUtils.isNotEmpty(containers)){
            for(Container container : containers){
                if (!container.isRunning()) {
                    LiteThreadPool.getInstance().execute(() -> {
                        try {
                            LOG.info("container start run, container:{}", container.toString());
                            container.run();
                        } catch (Throwable e) {
                            String errorMsg = "run container error,errMsg:" + e.getMessage();
                            LOG.error(errorMsg, e);
                            /**
                             * 设置任务为失败
                             */
                            AbstractContainer abstractContainer = (AbstractContainer) container;
                            ExecutorJobService executorJobService = ExecutorUtils.getExecutorJobService();
                            executorJobService.fail(abstractContainer.getExecutorJob().getId(), errorMsg);
                        }
                    });
                }
            }
        }
    }

}
