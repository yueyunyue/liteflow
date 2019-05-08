package cn.lite.flow.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-05-07
 **/
public class ParamUtils {


    /**将参数转化为map:
     * a=1;b=2 => {"a":1, "b": 2}
     * @param param
     * @return
     */
    public static Map<String, String> param2Map(String param) {

        if(StringUtils.isBlank(param)){
            return Collections.emptyMap();
        }
        Map<String, String> paramMap = Maps.newHashMap();

        String[] paramArray = StringUtils.split(param, CommonConstants.SEMICOLON);
        for(String item : paramArray){
            String[] itemArray = StringUtils.split(item, CommonConstants.EQUAL);
            paramMap.put(itemArray[0], itemArray[1]);
        }

        return paramMap;
    }


}
