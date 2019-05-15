package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.job.ShellProcessJob;
import cn.lite.flow.executor.kernel.utils.JobUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;

/**
 * @description: java进程容器
 * @author: yueyunyue
 * @create: 2018-08-17
 **/
public class DirectShellContainer extends SyncContainer {

    private ShellProcessJob shellProcessJob;

    private Logger logger;

    public DirectShellContainer(ExecutorJob executorJob) {
        super(executorJob);
        logger = JobUtils.getLogger(executorJob);
    }

    @Override
    public void runInternal() throws Exception {
        String config = executorJob.getConfig();
        Props sysProps = new Props();
        Props props = new Props(config);
        String jobWorkspace = JobUtils.getWorkspace(executorJob);
        Logger logger = JobUtils.getLogger(executorJob);
        /**
         * 生成脚本文件
         */
        String content = props.getString(Constants.SHELL_CONTENT);
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
            logger.info("kill shell container executorJobId:{} get applicationId:{}", executorJob.getId(), executorJob.getApplicationId());
        } catch (Throwable e) {
            logger.error("kill shell container, jobId:" + executorJob.getId(), e);
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
