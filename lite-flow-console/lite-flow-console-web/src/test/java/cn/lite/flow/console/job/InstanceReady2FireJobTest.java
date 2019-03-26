package cn.lite.flow.console.job;

import cn.lite.flow.console.kernel.job.ConsumerEventQueueJob;
import cn.lite.flow.console.kernel.job.InstanceReady2FireJob;
import cn.lite.flow.console.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ly on 2019/1/4.
 */
public class InstanceReady2FireJobTest extends BaseTest {

    @Autowired
    private InstanceReady2FireJob instanceReady2FireJob;
    @Autowired
    private ConsumerEventQueueJob consumerEventQueueJob;

    @Test
    public void test() {
        instanceReady2FireJob.execute();
        consumerEventQueueJob.execute();
    }
}
