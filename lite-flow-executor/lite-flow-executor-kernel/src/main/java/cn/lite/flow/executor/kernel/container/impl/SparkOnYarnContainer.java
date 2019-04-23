package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.kernel.utils.YarnUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: spark on yarn
 * @author: yueyunyue
 * @create: 2019-04-10
 **/
public class SparkOnYarnContainer extends AsyncContainer {

    private final static Logger LOG = LoggerFactory.getLogger(SparkOnYarnContainer.class);

    private SparkConf sparkConf;

    private YarnClient yarnClient;

    public SparkOnYarnContainer(ExecutorJob executorJob) {
        super(executorJob);
        initSparkBaseEnvs();
    }

    public static void initSparkBaseEnvs() {
        String sparkJar = "hdfs://hadoop2yarn/user/lite/spark-assembly/spark-assembly-2.0.2-hadoop2.6.0.jar";

        System.setProperty("spark.default.parallelism", "200");
        System.setProperty("spark.driver.maxResultSize", "16g");
        System.setProperty(
                "spark.executor.extraJavaOptions",
                "-XX:MaxPermSize=1024M -XX:+UseG1GC -XX:+UnlockDiagnosticVMOptions -XX:+G1SummarizeConcMark -XX:InitiatingHeapOccupancyPercent=35");
        System.setProperty("spark.driver.maxResultSize", "16g");
        System.setProperty("spark.rpc.message.maxSize", "1024");
        System.setProperty("spark.core.connection.ack.wait.timeout", "300");
        System.setProperty("spark.rpc.askTimeout", "300");
        System.setProperty("spark.storage.memoryFraction", "0.2");
        System.setProperty("spark.shuffle.memoryFraction", "0.3");
        System.setProperty("spark.network.timeout", "300");
        System.setProperty("spark.sql.shuffle.partitions", "800");
        System.setProperty("spark.sql.autoBroadcastJoinThreshold", "400000000");
        System.setProperty("spark.sql.crossJoin.enabled", "true");
        System.setProperty("spark.shuffle.io.maxRetries", "30");
        System.setProperty("spark.shuffle.io.retryWait", "10s");
        System.setProperty("spark.shuffle.io.numConnectionsPerPeer", "2");
        System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        System.setProperty("spark.yarn.executor.memoryOverhead", "3000");
        System.setProperty("spark.yarn.jars", sparkJar);
        System.setProperty("spark.jars",sparkJar);
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        System.setProperty("SPARK_YARN_MODE", "true");
        System.setProperty("SPARK_SUBMIT", "true");
        System.setProperty("spark.master", "yarn");
        System.setProperty("spark.submit.deployMode", "cluster");
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

    private SparkConf initSparkConf(JSONObject configObj){

        String jobName = configObj.getString(CommonConstants.PARAM_EXECUTOR_JOB_NAME);
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(jobName);

        sparkConf.set("spark.app.name", jobName);
        sparkConf.set("spark.yarn.queue", "default");
//        sparkConf.set("spark.yarn.maxAppAttempts", String.valueOf(MAX_APP_ATTEMPTS));

        sparkConf.set("spark.driver.memory", "100m");
        sparkConf.set("spark.driver.cores", "1");
        sparkConf.set("spark.executor.memory","100m");
        sparkConf.set("spark.executor.cores", "1");
        // 设置并发实例数
        int instanceNum = 1;
        if (false) {
            sparkConf.set("spark.shuffle.service.enabled", "true");
            sparkConf.set("spark.dynamicAllocation.enabled", "true");
            sparkConf.set("spark.dynamicAllocation.minExecutors", "1");
            sparkConf.set("spark.dynamicAllocation.maxExecutors", String.valueOf(instanceNum));
        } else {
            sparkConf.set("spark.executor.instances", String.valueOf(instanceNum));
        }
        sparkConf.set("spark.yarn.stagingDir", "hdfs://hadoop2yarn/user/lite");

        /**
         * hadoop、hive配置文件
         */
        String hadoopFiles = getHadoopFiles();
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
        argList.add("cn.lite.flow.hello.word.HelloLiteFlow");
        /**
         * 添加jar
         */
        argList.add(Constants.YARN_PARAM_JAR);
        argList.add("hdfs://hadoop2yarn/user/lite/jar/lite-flow-hello-word.jar");


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
//        ExecutorJobService executorJobService = ExecutorUtils.getExecutorJobService();
        /**
         * 这只运行状态
         */
        this.setStatus(ContainerStatus.RUNNING);
//        executorJob.setApplicationId(appId);
//        executorJobService.bindApplicationIdAndRun(executorJob.getId(), appId);
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

    private String getHadoopFiles(){
        String rootPath = "hdfs://hadoop2yarn/user/lite/config/";
        StringBuilder filesBuilder = new StringBuilder(rootPath)
                .append("core-site.xml,").append(rootPath)
                .append("hive-site.xml,").append(rootPath)
                .append("hdfs-site.xml,").append(rootPath)
                .append("yarn-site.xml,");

        return filesBuilder.toString();
    }

}
