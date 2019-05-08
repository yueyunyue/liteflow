package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.kernel.job.ShellProcessJob;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @description: java进程容器
 * @author: yueyunyue
 * @create: 2018-08-17
 **/
public class ShellContainer extends SyncContainer {

    private ShellProcessJob shellProcessJob;

    private final static Logger LOG = LoggerFactory.getLogger(ShellContainer.class);

    public ShellContainer(ExecutorJob executorJob) {
        super(executorJob);

    }

    @Override
    public void runInternal() throws Exception {
        String config = executorJob.getConfig();
        Props sysProps = new Props();
        Props props = new Props(config);
        String jobWorkspace = ExecutorMetadata.getJobWorkspace(executorJob.getId());
        Logger logger = ExecutorLoggerFactory.getLogger(executorJob.getId(), jobWorkspace);
        /**
         * 生成脚本文件
         */
        String content = props.getString(Constants.SHELL_COINTENT);
        String shellName = Constants.SHELL_SCRIPT_PREFIX + executorJob.getId() + CommonConstants.POINT + Constants.SHELL_COMMAND;
        String shellPath = jobWorkspace + CommonConstants.FILE_SPLIT + shellName;
        try {
            FileUtils.write(new File(shellPath), content);
            props.put(Constants.SHELL_SCRIPT_PATH, shellPath);
        } catch (Throwable e) {
            logger.error("job: {} write shell script error", this.executorJob.getId(), e);
            throw new ExecutorRuntimeException(e.getMessage());
        }

        this.shellProcessJob = new ShellProcessJob(executorJob.getId(), sysProps, props, logger);

        shellProcessJob.run();

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
