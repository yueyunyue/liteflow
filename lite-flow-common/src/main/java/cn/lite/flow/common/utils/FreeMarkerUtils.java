package cn.lite.flow.common.utils;

import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: freeMarker工具
 * @author: yueyunyue
 * @create: 2019-05-07
 **/
public class FreeMarkerUtils {

    private final static Logger LOG = LoggerFactory.getLogger(FreeMarkerUtils.class);

    public static String formatString(String content, Object paramMap) {
        String templateName = "lite-template";
        return formatString(templateName, content, paramMap);
    }

    /**
     * 处理字符串
     * @param templateName
     * @param content
     * @param paramMap
     * @return
     */
    public static String formatString(String templateName, String content, Object paramMap) {

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
        StringWriter writer = new StringWriter();
        try {
            Template template = new Template(templateName, content, configuration);
            template.process(paramMap, writer);
        } catch (Throwable e) {
            LOG.error("freemarker handler error");
            throw new RuntimeException(e);
        }
        return writer.toString();
    }



}
