package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.kernel.job.JavaProcessJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;

/**
 * @description: java进程容器
 * @author: yueyunyue
 * @create: 2018-08-17
 **/
public class JavaProcessContainer extends SyncContainer {

    private JavaProcessJob javaProcessJob;

    private String workDirPath;

    public JavaProcessContainer(ExecutorJob executorJob) throws Exception {
        super(executorJob);
    }

    @Override
    public void runInternal() throws Exception {
        try{
            String config = executorJob.getConfig();
            if(StringUtils.isNotBlank(config)){
                Props sysProps = new Props();
                Props props = new Props(config);

                this.workDirPath = ExecutorMetadata.getJobWorkspace(executorJob.getId());

                Logger logger = ExecutorLoggerFactory.getLogger(executorJob.getId(), workDirPath);

                String configFilePath = this.workDirPath + CommonConstants.FILE_SPLIT + executorJob.getId() + Constants.CONFIG_FILE_SUFFIX;
                FileUtils.write(new File(configFilePath), config);
                props.put(Constants.JAVA_MAIN_ARGS, configFilePath);

                this.javaProcessJob = new JavaProcessJob(executorJob.getId(),sysProps, props, logger);
                javaProcessJob.run();

            }else{
                throw new IllegalArgumentException("execute job config is empty");
            }
        } finally {
            ExecutorLoggerFactory.stop(executorJob.getId());
        }
    }

    @Override
    public void kill() throws Exception {
        javaProcessJob.cancel();
    }

    @Override
    public boolean isFailed() {
        boolean canceled = javaProcessJob.isCanceled();
        if(canceled){
            super.setStatus(ContainerStatus.FAIL);
        }
        return canceled;
    }

    @Override
    public boolean isSuccess() {
        boolean success = javaProcessJob.isSuccess();
        if(success){
            super.setStatus(ContainerStatus.SUCCESS);
        }
        return success;
    }

}
