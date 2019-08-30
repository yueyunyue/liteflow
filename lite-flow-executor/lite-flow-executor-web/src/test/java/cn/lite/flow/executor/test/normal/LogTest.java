package cn.lite.flow.executor.test.normal;

import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-01-25
 **/
public class LogTest {

    @Test
    public void testLogFactory(){

        Logger logger = ExecutorLoggerFactory.getLogger(10,"/tmp/log");
        for (int i = 0; i < 10; i ++) {
            logger.info("===info:{}===", i);
            logger.error("===error:{}===", i);
        }

    }

}
