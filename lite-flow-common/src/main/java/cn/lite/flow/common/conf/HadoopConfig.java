package cn.lite.flow.common.conf;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.HadoopUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @description: hadoop相关配置
 * @author: yueyunyue
 * @create: 2019-04-25
 **/
public class HadoopConfig implements InitializingBean {

    @Value("${hadoop.userName:}")
    private String hadoopUserName;

    @Value("${hadoop.spark.jarPath:}")
    private String sparkJarPath;

    @Value("${hadoop.spark.yarn.stagingDir:}")
    private String sparkYarnStagingDir;

    /**
     * 初始化hadoop相关参数
     */
    private void initHadoopProperties(){
        if(StringUtils.isNotBlank(hadoopUserName)){
            System.setProperty("HADOOP_USER_NAME", hadoopUserName);
        }

    }

    /**
     * 获取spark的jar包
     * @return
     */
    private String getSparkJars(){
        List<String> files = HadoopUtils.listFileOrDirFileNames(sparkJarPath);
        if(CollectionUtils.isNotEmpty(files)){
            String filePaths = StringUtils.join(files, CommonConstants.COMMA);
            return filePaths;
        }
        return null;

    }

    /**
     * 初始化spark参数
     */
    public void initSparkProperties() {

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

        System.setProperty("SPARK_YARN_MODE", "true");
        System.setProperty("SPARK_SUBMIT", "true");
        System.setProperty("spark.master", "yarn");
        System.setProperty("spark.submit.deployMode", "cluster");
        /**
         * 指定spark相关的jar包
         */
        if(StringUtils.isNotBlank(sparkJarPath)){
            System.setProperty("spark.yarn.jars", getSparkJars());
            System.setProperty("spark.jars",getSparkJars());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        initHadoopProperties();

        initSparkProperties();
    }


}
