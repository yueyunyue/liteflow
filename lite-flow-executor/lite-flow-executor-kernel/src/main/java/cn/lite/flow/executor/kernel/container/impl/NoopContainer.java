package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.RandomUtils;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: java进程容器
 * @author: yueyunyue
 * @create: 2018-08-17
 **/
public class NoopContainer extends SyncContainer {

    private final static Logger LOG = LoggerFactory.getLogger(NoopContainer.class);

    public NoopContainer(ExecutorJob executorJob) {
        super(executorJob);
    }

    @Override
    public void runInternal() {
        Long executorJobId = executorJob.getId();
        String applicationId = executorJob.getSourceId() + CommonConstants.LINE + RandomUtils.digital(Constants.NOOP_ID_RANDOM_SIZE);
        ExecutorJobService executorJobService = ExecutorServiceUtils.getExecutorJobService();
        /**
         * executorJobId和绑定id,并标记任务开始
         */
        LOG.info("noop container executorJobId:{} get applicationId:{}", executorJobId, applicationId);
        this.executorJob.setApplicationId(applicationId);

        executorJobService.bindApplicationId(executorJobId, applicationId);
        /**
         * 直接设置成功
         */
        executorJobService.run(executorJobId);
        executorJobService.success(executorJobId);
    }

    @Override
    public void kill() {
        LOG.info("kill noop container executorJobId:{} get applicationId:{}", executorJob.getId(), executorJob.getApplicationId());
    }

    @Override
    public boolean isFailed() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
