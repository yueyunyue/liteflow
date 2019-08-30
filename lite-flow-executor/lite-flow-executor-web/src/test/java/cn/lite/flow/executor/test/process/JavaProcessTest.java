package cn.lite.flow.executor.test.process;

import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.job.JavaProcessJob;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2018-08-23
 **/
public class JavaProcessTest {

    private Logger logger = LoggerFactory.getLogger(JavaProcessTest.class);

    @Test
    public void testHello() throws Exception {
        String mainClass = "cn.lite.flow.hello.word.HelloLiteFlow";
        String jarPath = "/Users/yueyunyue/workspace4m/lite-flow/docs/jars";

        String jvmArgs = "-Xmn256m -Xmx256m";
        logger.info(mainClass);

        Props props = new Props();
        props.put(Constants.JAVA_MAIN_CLASS, mainClass);
//        props.put(Constants.WORKING_DIR, jarPath);
        props.put(Constants.JAVA_JVM_PARAMS, jvmArgs);
        props.put(Constants.JAVA_MAIN_JAR_PATH, jarPath);
        props.put(Constants.JAVA_MAIN_ARGS, jarPath + "/config.json");

        Props sysProps = new Props();

        JavaProcessJob javaProcessJob = new JavaProcessJob(0l,sysProps, props, logger);

        javaProcessJob.run();

        System.in.read();
    }


}
