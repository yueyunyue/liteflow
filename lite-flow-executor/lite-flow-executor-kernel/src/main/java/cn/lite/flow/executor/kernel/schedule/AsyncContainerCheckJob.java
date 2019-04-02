package cn.lite.flow.executor.kernel.schedule;

import cn.lite.flow.common.job.basic.AbstractUnstatefullJob;
import cn.lite.flow.executor.common.utils.ContainerMetadata;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @description: 查看异步任务是否成功
 * @author: yueyunyue
 * @create: 2019-01-17
 **/
public class AsyncContainerCheckJob extends AbstractUnstatefullJob {

    @Override
    public void executeInternal() {
        /**
         * 处理校验异步任务是否成功
         */
        List<AsyncContainer> asyncContainers = ContainerMetadata.getAsyncContainers();
        if(CollectionUtils.isNotEmpty(asyncContainers)){
            for(AsyncContainer container : asyncContainers){
                if (container.isRunning()) {
                    try {
                        /**
                         * 校验状态
                         */
                        container.checkStatus();
                        /**
                         * 如果是最终状态,success 或 fail
                         */
                        boolean isFinish = container.isFinish();
                        if(isFinish){
                            ExecutorJob executorJob = container.getExecutorJob();
                            ContainerMetadata.removeContainer(executorJob.getId());
                            LOG.info("execute job is success,id:{}, applicationId:{}", executorJob.getId(), executorJob.getApplicationId());
                        }
                    } catch (Throwable e) {
                        String errorMsg = "check async container success status error,errMsg:" + e.getMessage();
                        LOG.error(errorMsg, e);
                    }
                }
            }
        }
    }
}
