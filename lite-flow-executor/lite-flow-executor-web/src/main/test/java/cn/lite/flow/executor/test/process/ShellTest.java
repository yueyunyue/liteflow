package cn.lite.flow.executor.test.process;

import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.job.ShellProcessJob;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: shell
 * @author: yueyunyue
 * @create: 2018-08-24
 **/
public class ShellTest {

    private Logger logger = LoggerFactory.getLogger(ShellTest.class);
    @Test
    public void testShell() throws Exception {
        String shell = "/Users/yueyunyue/workspace4m/liteflow/docs/jars/paramTest.sh";
        Props sysProps = new Props();
        Props props = new Props();
        props.put(Constants.SHELL_SCRIPT_PATH, shell);

        ShellProcessJob shellProcessJob = new ShellProcessJob(100,sysProps, props, logger);

        shellProcessJob.run();
        System.in.read();

    }

}
