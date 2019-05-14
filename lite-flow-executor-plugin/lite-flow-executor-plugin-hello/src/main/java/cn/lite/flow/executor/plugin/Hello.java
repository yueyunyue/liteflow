package cn.lite.flow.executor.plugin;

import cn.lite.flow.common.model.consts.CommonConstants;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @description: hello
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class Hello {

    private final static Logger LOG = LoggerFactory.getLogger(Hello.class);

    public static void main(String[] args) throws IOException {
        LOG.info("args is {}", args);
        String arg0 = args[0];
        String data = FileUtils.readFileToString(new File(arg0), CommonConstants.UTF8_CHARSET);
        LOG.info("config is {}" + data);
        System.exit(CommonConstants.SYSTEM_EXIT_SUCCESS);
    }

}
