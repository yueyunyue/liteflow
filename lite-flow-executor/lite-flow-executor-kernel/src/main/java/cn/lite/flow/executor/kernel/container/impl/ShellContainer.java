package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.FreeMarkerUtils;
import cn.lite.flow.common.utils.ParamExpressionUtils;
import cn.lite.flow.common.utils.ParamUtils;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.kernel.job.ShellProcessJob;
import cn.lite.flow.executor.kernel.utils.JobUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorFileUtils;
import cn.lite.flow.executor.service.utils.ExecutorServiceUtils;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;


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
        try {
            String config = executorJob.getConfig();
            Props sysProps = new Props();
            Props props = new Props(config);
            String jobWorkspace = JobUtils.getWorkspace(executorJob);
            Logger logger = JobUtils.getLogger(executorJob);
            /**
             * 生成脚本文件
             */
            String filePath = props.getString(CommonConstants.PARAM_FILE);
            String param = props.getString(CommonConstants.PARAM);
            String shellName = Constants.SHELL_SCRIPT_PREFIX + executorJob.getId() + CommonConstants.POINT + Constants.SHELL_COMMAND;
            String shellPath = jobWorkspace + CommonConstants.FILE_SPLIT + shellName;

            Map<String, String> jobParamMap = props.getParamMap();
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.putAll(jobParamMap);
            try {
                /**
                 * 1.如果已经传了shell内容,直接使用；
                 * 2.通过附件上传的需要把内容拿下来
                 */
                String shellContent = props.getString(CommonConstants.PARAM_CONTENT);
                if(StringUtils.isBlank(shellContent)){
                    shellContent = ExecutorFileUtils.getFileContent(filePath);
                }
                logger.info("job:{} get origin shell content:{}", executorJob.getId(), shellContent);
                /**
                 * 参数不为空，替换关键字
                 */
                if(StringUtils.isNotBlank(param)){
                    paramMap.putAll(ParamUtils.param2Map(param));
                }
                shellContent = ParamExpressionUtils.handleParam(shellContent, paramMap);
                logger.info("job:{} handle shell param:{} content:{}", executorJob.getId(), paramMap.toString(), shellContent);
                FileUtils.write(new File(shellPath), shellContent);
                props.put(Constants.SHELL_SCRIPT_PATH, shellPath);
            } catch (Throwable e) {
                logger.error("job: {} write shell script error", this.executorJob.getId(), e);
                throw new ExecutorRuntimeException(e.getMessage());
            }

            this.shellProcessJob = new ShellProcessJob(executorJob.getId(), sysProps, props, logger);

            shellProcessJob.run();

        } finally {
            ExecutorLoggerFactory.stop(executorJob.getId());
        }
    }

    @Override
    public void kill() {
        try {
            shellProcessJob.cancel();
            LOG.info("kill remote shell container executorJobId:{} get applicationId:{}", executorJob.getId(), executorJob.getApplicationId());
        } catch (Throwable e) {
            LOG.error("kill remote shell container, jobId:" + executorJob.getId(), e);
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
