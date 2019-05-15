package cn.lite.flow.common.test.freemarker;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.FreeMarkerUtils;
import cn.lite.flow.common.utils.ParamExpressionUtils;
import cn.lite.flow.common.utils.ParamUtils;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-05-07
 **/
public class ParamTest {

    @Test
    public void test(){

        Map<String, Object> param = Maps.newHashMap();

        param.put("name", "12321");
        param.put(CommonConstants.PARAM_CONSOLE_TASK_VERSION, 20190515);

        String template = "${time:YYYYMMDD,yesterday} hello,${name}";
        String formatString = ParamExpressionUtils.handleParam(template, param);
        System.out.println(formatString);
    }


}
