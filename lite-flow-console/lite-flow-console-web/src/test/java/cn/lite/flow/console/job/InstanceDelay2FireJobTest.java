package cn.lite.flow.console.job;

import cn.lite.flow.console.kernel.job.InstanceRunDelayAlarmJob;
import cn.lite.flow.console.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by luya on 2019/1/4.
 */
public class InstanceDelay2FireJobTest extends BaseTest {

    @Autowired
    private InstanceRunDelayAlarmJob instanceRunDelayAlarmJob;

    @Test
    public void test() {
        instanceRunDelayAlarmJob.execute();
    }
}
