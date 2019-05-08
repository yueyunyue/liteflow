package cn.lite.flow.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @description: url相关工具
 * @author: yueyunyue
 * @create: 2019-01-10
 **/
public class CommonFileUtils {

    /**
     * 获取协议
     * @param url
     * @return
     */
    public static String getProtocolAndDomain(String url){
        if(StringUtils.isBlank(url)){
            return null;
        }
        String[] urlArray = StringUtils.split(url, CommonConstants.URL_QUESTION);
        return urlArray[0];
    }

    /**
     * 获取请求参数
     * @param url
     * @return
     */
    public static Map<String, String> getAttachmentParamMap(String url){

        if(StringUtils.isBlank(url)){
            return null;
        }
        Map<String, String> paramMap = Maps.newHashMap();
        String[] urlArray = StringUtils.split(url, CommonConstants.URL_QUESTION);
        if(urlArray.length == 2){
            String urlParamStr = urlArray[1];
            String[] paramArray = StringUtils.split(urlParamStr, CommonConstants.URL_PARAM_SPLIT);
            for(String paramItem : paramArray){
                String[] paramItemArray = StringUtils.split(paramItem, CommonConstants.EQUAL);
                if(paramItemArray.length == 2){
                    paramMap.put(paramItemArray[0], paramItemArray[1]);
                }
            }
        }
        return paramMap;
    }


}
