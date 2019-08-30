package cn.lite.flow.executor.test.container;

import cn.lite.flow.executor.kernel.container.ContainerFactory;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.Container;
import cn.lite.flow.executor.service.ExecutorContainerService;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.test.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 容器测试
 * @author: yueyunyue
 * @create: 2019-01-27
 **/
public class ContainerTest extends BaseTest {

    @Autowired
    private ExecutorContainerService containerService;

    @Autowired
    private ExecutorJobService jobService;


    @Test
    public void testJavaProcess() throws Exception {

        long jobId = 5L;

        ExecutorJob executorJob = jobService.getById(jobId);
        Container container = ContainerFactory.newInstance(executorJob);
        container.run();


    }

}
