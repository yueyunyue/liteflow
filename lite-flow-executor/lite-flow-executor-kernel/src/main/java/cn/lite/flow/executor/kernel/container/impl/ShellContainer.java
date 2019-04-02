package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.kernel.job.ShellProcessJob;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: java进程容器
 * @author: yueyunyue
 * @create: 2018-08-17
 **/
public class ShellContainer extends SyncContainer {

    private final ShellProcessJob shellProcessJob;

    private final static Logger LOG = LoggerFactory.getLogger(ShellContainer.class);

    public ShellContainer(ExecutorJob executorJob) {
        super(executorJob);
        String config = executorJob.getConfig();
        Props sysProps = new Props();
        Props props = new Props(config);
        String jobWorkspace = ExecutorMetadata.getJobWorkspace(executorJob.getId());
        Logger logger = ExecutorLoggerFactory.getLogger(executorJob.getId(), jobWorkspace);

        this.shellProcessJob = new ShellProcessJob(executorJob.getId(), sysProps, props, logger);

    }

    @Override
    public void runInternal() throws Exception {
        try {

            shellProcessJob.run();

        } finally {

        }
    }

    @Override
    public void kill() {
        try {
            shellProcessJob.cancel();
            LOG.info("kill shell container executorJobId:{} get applicationId:{}", executorJob.getId(), executorJob.getApplicationId());
        } catch (Throwable e) {
            LOG.error("kill shell container, jobId:" + executorJob.getId(), e);
            throw new ExecutorRuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean isFailed() {
        return shellProcessJob.isCanceled();
    }

    @Override
    public boolean isSuccess() {
        return shellProcessJob.isSuccess();
    }
}
