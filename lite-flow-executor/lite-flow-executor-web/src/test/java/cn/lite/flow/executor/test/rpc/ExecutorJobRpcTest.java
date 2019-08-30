package cn.lite.flow.executor.test.rpc;

import cn.lite.flow.executor.client.ExecutorJobRpcService;
import cn.lite.flow.executor.test.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: rpc测试
 * @author: yueyunyue
 * @create: 2019-01-27
 **/
public class ExecutorJobRpcTest extends BaseTest {


    @Autowired
    private ExecutorJobRpcService jobService;


    @Test
    public void testJavaProcess() throws Exception {

        String log = jobService.getLog(11);
        System.out.println(log);

    }

}
