package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.kernel.utils.YarnUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @description: spark on yarn
 * @author: yueyunyue
 * @create: 2019-04-10
 **/
public class SparkOnYarnContainer extends AsyncContainer {

    private final static Logger LOG = LoggerFactory.getLogger(SparkOnYarnContainer.class);

    private final SparkConf sparkConf;

    private YarnClient yarnClient;

    public SparkOnYarnContainer(ExecutorJob executorJob) {
        super(executorJob);
        sparkConf = new SparkConf(false);

    }

    /**
     * 获取yarn applicationId
     * @return
     */
    private ApplicationId getApplicationId(){
        String appId = this.executorJob.getApplicationId();
        ApplicationId applicationId = YarnUtils.convertApplicationId(appId);
        return applicationId;
    }

    @Override
    public void checkStatus() {
        ApplicationId applicationId = this.getApplicationId();
        if(applicationId == null){
            return;
        }
        try {
            ApplicationReport applicationReport = yarnClient.getApplicationReport(applicationId);
            ExecutorJobService executorJobService = ExecutorUtils.getExecutorJobService();
            YarnApplicationState yarnApplicationState = applicationReport.getYarnApplicationState();

            switch (yarnApplicationState){
                case FINISHED:
                    executorJobService.success(this.getExecutorJob().getId());
                    break;
                case FAILED:
                    executorJobService.fail(this.getExecutorJob().getId(), applicationReport.getDiagnostics());
                    break;
                case KILLED:
                    executorJobService.fail(this.getExecutorJob().getId(), "killed by other");
                    break;
                default:
                    LOG.info("check job:{}(applicationId:{}) status is {}", executorJob.getId(), executorJob.getApplicationId(), yarnApplicationState.name());
            }
        } catch (Throwable e) {
            LOG.error("job:{} check status error", executorJob.getId(), e);
        }

    }

    @Override
    public void run() throws Exception {

        ExecutorJob executorJob = this.getExecutorJob();

        /**
         * 初始化spark conf
         */


        /**
         * 生成用户参数
         */
        String[] args = new String[10];
        ClientArguments clientArgs = new ClientArguments(args);
        /**
         * 提交到yarn
         */
        Client client = new Client(clientArgs, sparkConf);
        ApplicationId applicationId = client.submitApplication();
        String appId = applicationId.toString();
        ExecutorJobService executorJobService = ExecutorUtils.getExecutorJobService();
        /**
         * 这只运行状态
         */
        this.setStatus(ContainerStatus.RUNNING);
        executorJob.setApplicationId(appId);
        executorJobService.bindApplicationIdAndRun(executorJob.getId(), appId);
    }

    @Override
    public void kill() throws Exception {
        ApplicationId applicationId = this.getApplicationId();
        if(applicationId == null){
            return;
        }
        try {
            yarnClient.killApplication(applicationId);
        } catch (Throwable e) {
            LOG.error("kill job:{} error", executorJob.getId(), e);
            throw new ExecutorRuntimeException(e.getMessage());
        }
    }

}
