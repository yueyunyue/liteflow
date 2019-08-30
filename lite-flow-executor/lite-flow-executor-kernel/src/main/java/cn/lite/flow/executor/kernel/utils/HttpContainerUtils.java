package cn.lite.flow.executor.kernel.utils;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.consts.CommonConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * @description: http容器相关工具
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
public class HttpContainerUtils {

    /**
     * 转换参数，将a=a;b=b;c=c转换为数组
     * @param param
     * @return
     */
    public static List<Tuple<String, String>> covert2Params(String param){
        if(StringUtils.isBlank(param)){
            return null;
        }

        List<Tuple<String, String>> params = Lists.newArrayList();

        String[] paramFullArray = StringUtils.split(param, CommonConstants.SEMICOLON);
        for(String paramFullItem : paramFullArray){
            String[] paramFullItemArray = StringUtils.split(paramFullItem, CommonConstants.EQUAL);
            int length = paramFullItemArray.length;
            String key = paramFullItemArray[NumberUtils.INTEGER_ZERO];
            String value = "";
            if(length == NumberUtils.INTEGER_TWO){
                value = paramFullItemArray[NumberUtils.INTEGER_ONE];
            }
            params.add(Tuple.of(key, value));
        }

        return params;

    }

}
