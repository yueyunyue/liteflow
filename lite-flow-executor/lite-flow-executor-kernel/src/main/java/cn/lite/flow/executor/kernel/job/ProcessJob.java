package cn.lite.flow.executor.kernel.job;

import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.CommandUtils;
import cn.lite.flow.executor.common.utils.LiteThreadPool;
import cn.lite.flow.executor.common.utils.ProcessorUtils;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.process.LiteProcess;
import cn.lite.flow.executor.kernel.process.ProcessFailureException;
import cn.lite.flow.executor.kernel.utils.LiteProcessBuilder;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorServiceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 进程任务
 * @author: yueyunyue
 * @create: 2018-08-17
 **/
public class ProcessJob extends AbstractJob {

    private List<LiteProcess> processes;

    private Long softKillTime = 500L;

    protected ProcessJob(long executorJobId, Props sysProps, Props jobProps, Logger log) {
        super(executorJobId, sysProps, jobProps, log);
        processes = Lists.newArrayList();
    }

    @Override
    public void run() throws Exception {

        List<String> commandList = getCommandList();
        if(CollectionUtils.isEmpty(commandList)){
            throw new IllegalArgumentException("command can not be empty");
        }

        ExecutorJobService executorJobService = ExecutorServiceUtils.getExecutorJobService();
        /**
         * 任务运行
         */
        executorJobService.run(executorJobId);

        for(String command : commandList){
            String[] commandLine = CommandUtils.partitionCommandLine(command);
            logger.info("executeJobId:{} run, command: {}", executorJobId, commandLine);
            LiteProcessBuilder processBuilder = new LiteProcessBuilder(commandLine);
            //添加logger
            processBuilder.setLogger(this.logger);
            //添加环境变量
            Map<String, String> env = getEnv();
            if(env != null && env.size() > 0){
                processBuilder.setEnv(env);
            }
            //添加工作目录
            String workingDir = getWorkingDir();
            if(StringUtils.isNotBlank(workingDir)){
                processBuilder.setWorkingDir(workingDir);
            }

            LiteProcess liteProcess = processBuilder.build();
            processes.add(liteProcess);

            LiteThreadPool.getInstance().execute(() -> {
                try {
                    liteProcess.run();
                    /**
                     * 进程正常退出，说明运行成功
                     */
                    logger.info("job run success, executorJobId:{}", executorJobId);
                    executorJobService.success(executorJobId);
                } catch (Throwable e) {
                    String message = "process run error, executorJobId:"+ executorJobId;
                    if(e instanceof ProcessFailureException){
                        ProcessFailureException failureException = (ProcessFailureException)e;
                        String errorMsg = failureException.getErrorMsg();
                        int exitCode = failureException.getExitCode();
                        message = message + " ,process exitCode:" + exitCode + ", errorMsg:" + errorMsg;
                    }else{
                        message = message + ", errorMsg:" + e.getMessage();
                    }
                    logger.error(message, e);
                    /**
                     * 进程抛异常，说明运行失败
                     */
                    executorJobService.fail(executorJobId, message);

                }
            });

            liteProcess.awaitStartup();
            int processId = liteProcess.getProcessId();
            /**
             * executorJobId和进程绑定,并标记任务开始
             */
            String applicationId = ProcessorUtils.generateApplicationId(executorJobId, processId);
            logger.info("executorJobId:{} get processId:{} generate applicationId:{}", executorJobId, processId, applicationId);
            executorJobService.bindApplicationId(executorJobId, applicationId);
        }

    }

    public Map<String, String> getEnv() {
        final Map<String, String> envMap = jobProps.getMapByPrefix(Constants.ENV_PREFIX);
        envMap.putAll(jobProps.getMapByPrefix(Constants.ENV_PREFIX_UCASE));
        return envMap;
    }

    public String getWorkingDir() {
        final String workingDir = jobProps.getString(Constants.WORKING_DIR, "");
        if (workingDir == null) {
            return "";
        }

        return workingDir;
    }

    protected List<String> getCommandList(){
        return null;
    }


    @Override
    public void cancel() throws Exception {
        if(CollectionUtils.isNotEmpty(processes)){
            for(LiteProcess process : processes){
                try {
                    if(!process.isComplete()){
                        if(!process.softKill(softKillTime, TimeUnit.MILLISECONDS)){
                            process.hardKill();
                        }
                    }
                }catch (Throwable e){
                    logger.error("kill process error", e);
                    throw new ExecutorRuntimeException(e.getMessage());
                }
            }
        }

    }
}
