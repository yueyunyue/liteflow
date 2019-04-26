package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.conf.HadoopConfig;
import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.JSONUtils;
import cn.lite.flow.common.utils.YarnClientHolder;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.kernel.utils.YarnUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @description: spark on yarn
 * @author: yueyunyue
 * @create: 2019-04-10
 **/
public class SparkOnYarnContainer extends AsyncContainer {

    private final static Logger LOG = LoggerFactory.getLogger(SparkOnYarnContainer.class);

    private SparkConf sparkConf;

    public SparkOnYarnContainer(ExecutorJob executorJob) {
        super(executorJob);
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
            ApplicationReport applicationReport = YarnClientHolder.getYarnClient().getApplicationReport(applicationId);
            ExecutorJobService executorJobService = ExecutorUtils.getExecutorJobService();
            YarnApplicationState yarnApplicationState = applicationReport.getYarnApplicationState();

            LOG.info("check job:{}(applicationId:{}) status is {}", executorJob.getId(), executorJob.getApplicationId(), yarnApplicationState.name());
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
                    return;
            }
        } catch (Throwable e) {
            LOG.error("job:{} check status error", executorJob.getId(), e);
        }

    }

    private SparkConf initSparkConf(JSONObject configObj){

        String jobName = configObj.getString(CommonConstants.PARAM_EXECUTOR_JOB_NAME);

        String yarnQueue = configObj.getString(CommonConstants.SPARK_PARAM_YARN_QUEUE);
        String instanceNum = configObj.getString(CommonConstants.SPARK_PARAM_INSTANCE_NUM);

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(jobName);

        sparkConf.set("spark.app.name", jobName);
        sparkConf.set("spark.yarn.queue", yarnQueue);

        sparkConf.set("spark.driver.cores", configObj.getString(CommonConstants.SPARK_PARAM_DRIVER_CORES));
        sparkConf.set("spark.driver.memory", configObj.getString(CommonConstants.SPARK_PARAM_DRIVER_MEMORY) + CommonConstants.SPARK_PARAM_MEMORY_UNIT);
        sparkConf.set("spark.executor.cores", configObj.getString(CommonConstants.SPARK_PARAM_EXECUTOR_CORES));
        sparkConf.set("spark.executor.memory", configObj.getString(CommonConstants.SPARK_PARAM_EXECUTOR_MEMORY) + CommonConstants.SPARK_PARAM_MEMORY_UNIT);
        // 设置并发实例数
        if (HadoopConfig.getHadoopConf().isDynamicAllocation()) {
            sparkConf.set("spark.shuffle.service.enabled", "true");
            sparkConf.set("spark.dynamicAllocation.enabled", "true");
            sparkConf.set("spark.dynamicAllocation.minExecutors", "1");
            sparkConf.set("spark.dynamicAllocation.maxExecutors", String.valueOf(instanceNum));
        } else {
            sparkConf.set("spark.executor.instances", String.valueOf(instanceNum));
        }

        /**
         * hadoop、hive配置文件
         */
        String hadoopFiles = HadoopConfig.getHadoopConf().getSparkYarnDistFiles();
        sparkConf.set("spark.yarn.dist.files", hadoopFiles);

        return sparkConf;

    }

    /**
     * 获取参数
     * @param configObj
     * @return
     */
    private ClientArguments getArgs(JSONObject configObj){
        List<String> argList = Lists.newArrayList();

        /**
         * 添加主类
         */
        argList.add(Constants.YARN_PARAM_CLASS);
        argList.add(configObj.getString(CommonConstants.SPARK_PARAM_YARN_MAIN_CLASS));
        /**
         * 添加主类所在jar
         */
        argList.add(Constants.YARN_PARAM_JAR);
        argList.add(configObj.getString(CommonConstants.SPARK_PARAM_YARN_MAIN_JAR));

        /**
         * 添加配置文件
         */
        argList.add(Constants.YARN_PARAM_ARG);
        String configFilePath = this.generateConfigFile(JSONUtils.toJSONStringWithoutCircleDetect(configObj));
        argList.add(configFilePath);

        return new ClientArguments(argList.toArray(new String[]{}));

    }

    @Override
    public void run() throws Exception {

        ExecutorJob executorJob = this.getExecutorJob();

        String config = executorJob.getConfig();
        JSONObject configObj = null;
        if(StringUtils.isNotBlank(config)){
            configObj = JSONObject.parseObject(config);
        }

        /**
         * 初始化spark conf
         */
        this.sparkConf = initSparkConf(configObj);

        /**
         * 生成用户参数
         */
        ClientArguments clientArgs = getArgs(configObj);
        /**
         * 提交到yarn
         */
        Client client = new Client(clientArgs, this.sparkConf);
        ApplicationId applicationId = client.submitApplication();
        String appId = applicationId.toString();
        LOG.info("{} get yarn applicationId:{}", executorJob.getId(), appId);
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
            YarnClientHolder.getYarnClient().killApplication(applicationId);
        } catch (Throwable e) {
            LOG.error("kill job:{} error", executorJob.getId(), e);
            throw new ExecutorRuntimeException(e.getMessage());
        }
    }

    /**
     * 生成文件
     */
    private String generateConfigFile(String config){
        String workDirPath = ExecutorMetadata.getJobWorkspace(executorJob.getId());
        String configFilePath = workDirPath + CommonConstants.FILE_SPLIT + executorJob.getId() + Constants.CONFIG_FILE_SUFFIX;
        try {
            FileUtils.write(new File(configFilePath), config, CommonConstants.UTF8);
        } catch (Throwable e) {
            LOG.error("generate config file error", e);
            throw new ExecutorRuntimeException(e.getMessage());
        }
        return configFilePath;
    }

}
