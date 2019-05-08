package cn.lite.flow.common.test.freemarker;

import cn.lite.flow.common.utils.FreeMarkerUtils;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-05-07
 **/
public class FreemarkerTest {


    @Test
    public void test(){

        Map<String, Object> param = Maps.newHashMap();

        param.put("name", "12321");

        String template = "hello,${name}";
        String formatString = FreeMarkerUtils.formatString(template, param);
        System.out.println(formatString);

    }


}
