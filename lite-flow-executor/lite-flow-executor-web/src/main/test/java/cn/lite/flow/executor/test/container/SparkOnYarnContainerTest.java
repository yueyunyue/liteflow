package cn.lite.flow.executor.test.container;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.kernel.container.impl.SparkOnYarnContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import cn.lite.flow.executor.test.base.BaseTest;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * @description: 容器测试
 * @author: yueyunyue
 * @create: 2019-01-27
 **/
public class SparkOnYarnContainerTest extends BaseTest {

    @Test
    public void testSpark() throws Exception {

        ExecutorJob executorJob = new ExecutorJob();
        executorJob.setStatus( ExecutorJobStatus.NEW.getValue());


        JSONObject confObj = new JSONObject();
        confObj.put(CommonConstants.PARAM_EXECUTOR_JOB_NAME, "liteTest-" + System.currentTimeMillis());

        executorJob.setConfig(confObj.toJSONString());

        SparkOnYarnContainer container = new SparkOnYarnContainer(executorJob);
        container.run();

        while (true){
            Thread.sleep(1000l);
            container.checkStatus();
        }

    }

}
