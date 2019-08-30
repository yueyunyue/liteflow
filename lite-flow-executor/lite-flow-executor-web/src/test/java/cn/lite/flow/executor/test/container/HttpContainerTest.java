package cn.lite.flow.executor.test.container;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.HttpMethodType;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.kernel.container.impl.DirectShellContainer;
import cn.lite.flow.executor.kernel.container.impl.SyncHttpContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * @description: http容器测试
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
public class HttpContainerTest {


    @Test
    public void testSyncHttp() throws Exception {

        JSONObject param = new JSONObject();
        param.put(CommonConstants.HTTP_URL, "http://www.baidu.com");
        param.put(CommonConstants.HTTP_PARAM, "a=a;b=b");
        param.put(CommonConstants.HTTP_METHOD, HttpMethodType.GET.name());

        ExecutorJob job = new ExecutorJob();
        job.setId(1l);
        job.setConfig(param.toJSONString());
        job.setStatus(ExecutorJobStatus.NEW.getValue());

        SyncHttpContainer shellContainer = new SyncHttpContainer(job);
        shellContainer.run();



    }

}
