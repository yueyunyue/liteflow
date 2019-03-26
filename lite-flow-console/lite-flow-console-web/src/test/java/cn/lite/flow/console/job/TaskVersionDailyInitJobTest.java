package cn.lite.flow.console.job;

import cn.lite.flow.console.kernel.job.TaskVersionDailyInitJob;
import cn.lite.flow.console.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ly on 2019/1/4.
 */
public class TaskVersionDailyInitJobTest extends BaseTest {

    @Autowired
    private TaskVersionDailyInitJob taskVersionDailyInitJob;

    @Test
    public void test() {
        taskVersionDailyInitJob.execute();
    }
}
