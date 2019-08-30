package cn.lite.flow.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import com.google.common.collect.Maps;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

/**
 * @description: http容器相关工具
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
public class OkHttpUtils {

    /**
     * 转换参数，将a=a;b=b;c=c转换为数组
     * @param param
     * @return
     */
    public static Map<String, String> covert2Params(String param){
        if(StringUtils.isBlank(param)){
            return null;
        }

        Map<String, String> paramMap = Maps.newHashMap();

        String[] paramFullArray = StringUtils.split(param, CommonConstants.SEMICOLON);
        for(String paramFullItem : paramFullArray){
            String[] paramFullItemArray = StringUtils.split(paramFullItem, CommonConstants.EQUAL);
            int length = paramFullItemArray.length;
            String key = paramFullItemArray[NumberUtils.INTEGER_ZERO];
            String value = "";
            if(length == NumberUtils.INTEGER_TWO){
                value = paramFullItemArray[NumberUtils.INTEGER_ONE];
            }
            paramMap.put(key, value);
        }

        return paramMap;

    }

    /**
     * 获取post Request.Builder
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     */
    public static Request.Builder getGetBuilder(String url, Map<String, String> headerMap, Map<String, String> paramMap){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        Request.Builder requestBuilder = null;
        if(MapUtils.isNotEmpty(paramMap)){
            paramMap.forEach((key, value) -> {
                urlBuilder.addQueryParameter(key, value);
            });
            HttpUrl httpUrl = urlBuilder.build();
            requestBuilder = new Request.Builder().get().url(httpUrl);
        }

        addHeader2RequestBuilder(requestBuilder, headerMap);

        return requestBuilder;
    }

    /**
     * 获取post Request.Builder
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     */
    public static Request.Builder getPostBuilder(String url, Map<String, String> headerMap, Map<String, String> paramMap){
        Request.Builder requestBuilder = null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if(MapUtils.isNotEmpty(paramMap)){
            paramMap.forEach((key, value) -> {
                formBodyBuilder.add(key, value);
            });

        }
        requestBuilder = new Request.Builder().post(formBodyBuilder.build()).url(url);
        addHeader2RequestBuilder(requestBuilder, headerMap);
        return requestBuilder;
    }

    /**
     * 添加header
     * @param requestBuilder
     * @param headerMap
     */
    public static void addHeader2RequestBuilder(Request.Builder requestBuilder, Map<String, String> headerMap){
        /**
         * header处理
         */
        if(requestBuilder != null && MapUtils.isNotEmpty(headerMap)){
            headerMap.forEach((key, value) -> {
                requestBuilder.addHeader(key, key);
            });

        }
    }


}
