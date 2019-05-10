package cn.lite.flow.common.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @description: property工具
 * @author: yueyunyue
 * @create: 2019-05-10
 **/
public class PropertyUtils {

    private final static Logger LOG = LoggerFactory.getLogger(PropertyUtils.class);

    /**
     * 获取配置
     * @param fileName
     * @return
     */
    public static Properties getProperty(String fileName){
        FileInputStream fileInputStream = null;
        try {
            Properties props = new Properties();
            fileInputStream = FileUtils.openInputStream(new File(fileName));
            props.load(fileInputStream);
            return props;
        } catch (Throwable e) {
            LOG.error("get properties error,file:{}",fileName, e);
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(fileInputStream);
        }

    }

}
