package cn.lite.flow.executor.test.container;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.kernel.container.impl.SparkOnYarnContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * @description: 容器测试
 * @author: yueyunyue
 * @create: 2019-01-27
 **/
public class SparkOnYarnContainerTest {



    @Test
    public void testSpark() throws Exception {


        System.out.println("SPARK_YARN_MODE:" + System.getenv("SPARK_YARN_MODE"));
        System.out.println("SPARK_CONF_DIR:" + System.getenv("SPARK_CONF_DIR"));
        System.out.println("HADOOP_CONF_DIR:" + System.getenv("HADOOP_CONF_DIR"));
        System.out.println("YARN_CONF_DIR:" + System.getenv("YARN_CONF_DIR"));
        System.out.println("SPARK_KAFKA_VERSION:" + System.getenv("SPARK_KAFKA_VERSION"));
        System.out.println("HADOOP_HOME:" + System.getenv("HADOOP_HOME"));
        System.out.println("HADOOP_COMMON_HOME:" + System.getenv("HADOOP_COMMON_HOME"));
        System.out.println("SPARK_HOME:" + System.getenv("SPARK_HOME"));
        System.out.println("SPARK_DIST_CLASSPATH:" + System.getenv("SPARK_DIST_CLASSPATH"));
        System.out.println("SPARK_EXTRA_LIB_PATH:" + System.getenv("SPARK_EXTRA_LIB_PATH"));
        System.out.println("LD_LIBRARY_PATH:" + System.getenv("LD_LIBRARY_PATH"));


        ExecutorJob executorJob = new ExecutorJob();
        executorJob.setStatus( ExecutorJobStatus.NEW.getValue());

        JSONObject confObj = new JSONObject();
        confObj.put(CommonConstants.PARAM_EXECUTOR_JOB_NAME, "liteTest");

        executorJob.setConfig(confObj.toJSONString());

        SparkOnYarnContainer container = new SparkOnYarnContainer(executorJob);
        container.run();

    }

}
