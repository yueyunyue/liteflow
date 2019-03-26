package cn.lite.flow.console.job;

import cn.lite.flow.console.kernel.job.VersionDailyInit2FireJob;
import cn.lite.flow.console.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ly on 2019/1/4.
 */
public class VersionDailyInit2FireJobTest extends BaseTest {

    @Autowired
    private VersionDailyInit2FireJob versionDailyInit2FireJob;

    @Test
    public void test() {
        versionDailyInit2FireJob.execute();
    }
}
