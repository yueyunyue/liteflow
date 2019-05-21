package cn.lite.flow.executor.kernel.utils;

import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import org.slf4j.Logger;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-05-15
 **/
public class JobUtils {

    /**
     * 获取logger
     * @param executorJob
     * @return
     */
    public static Logger getLogger(ExecutorJob executorJob){
        String jobWorkspace = getWorkspace(executorJob);
        Logger logger = ExecutorLoggerFactory.getLogger(executorJob.getId(), jobWorkspace);
        return logger;
    }

    /**
     * 获取任务的工作目录
     * @param executorJob
     * @return
     */
    public static String getWorkspace(ExecutorJob executorJob){
        String jobWorkspace = ExecutorMetadata.getJobWorkspace(executorJob.getId());
        return jobWorkspace;
    }
}
