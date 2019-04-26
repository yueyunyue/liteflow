package cn.lite.flow.common.conf;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.HadoopUtils;
import cn.lite.flow.common.utils.SpringUtils;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: hadoop相关配置
 * @author: yueyunyue
 * @create: 2019-04-25
 **/
@Getter
public class HadoopConfig implements InitializingBean {

    private final static Logger LOG = LoggerFactory.getLogger(HadoopConfig.class);

    @Value("${hadoop.userName}")
    private String hadoopUserName;                       //调用hadoop的用户名

    @Value("${spark.distFiles:}")
    private String disFiles;                             //hadoop，hive，yarn文件所在文件夹

    @Value("${spark.jarPath:}")
    private String sparkJarPath;                         //spark jar包所在hdfs路径

    @Value("${spark.yarn.stagingDir:}")
    private String sparkYarnStagingDir;                  //spark staging的文件夹

    @Value("${spark.isDynamicAllocation:}")
    private boolean isDynamicAllocation;                 //spark是否可以动态获取资源

    @Value("${spark.executor.memoryOverhead:}")
    private String executorMemoryOverhead;                //spark memoryOverhead内存配置

    @Value("${spark.executor.dynamicAllocation.minExecutors:}")
    private String dynamicAllocationMinExecutors;         //spark memoryOverhead内存配置

    private AtomicBoolean isInit = new AtomicBoolean(false);

    /**
     * 初始化hadoop相关参数
     */
    private void initHadoopProperties(){
        System.setProperty("HADOOP_USER_NAME", hadoopUserName);

    }

    /**
     * 获取spark的jar包
     * @return
     */
    public String getSparkJars(){
        List<String> files = HadoopUtils.listFileOrDirFileNames(sparkJarPath);
        if(CollectionUtils.isNotEmpty(files)){
            String filePaths = StringUtils.join(files, CommonConstants.COMMA);
            return filePaths;
        }
        return "";

    }

    /**
     * 获取yarn相关文件:hive-site.xml、yarn-site.xml等
     * @return
     */
    public String getSparkYarnDistFiles(){
        List<String> files = HadoopUtils.listFileOrDirFileNames(disFiles);
        if(CollectionUtils.isNotEmpty(files)){
            String filePaths = StringUtils.join(files, CommonConstants.COMMA);
            return filePaths;
        }
        return "";

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
        System.setProperty("spark.yarn.executor.memoryOverhead", executorMemoryOverhead);

        if(isDynamicAllocation){
            System.setProperty("spark.shuffle.service.enabled", "true");
            System.setProperty("spark.dynamicAllocation.enabled", "true");
            System.setProperty("spark.dynamicAllocation.minExecutors", dynamicAllocationMinExecutors);
        }

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
        if(isInit.get()){
            LOG.info("already inited");
            return;
        }
        initHadoopProperties();
        initSparkProperties();
        isInit.set(true);
    }

    /**
     *
     * @return
     */
    public static HadoopConfig getHadoopConf(){
        HadoopConfig hadoopConfig = SpringUtils.getBean(HadoopConfig.class);
        return hadoopConfig;
    }


}
