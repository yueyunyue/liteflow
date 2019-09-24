package cn.lite.flow.common.model.consts;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
/**
 * http method枚举常量
 */
@Getter
public enum HttpMethodType {

    GET("本地"),

    POST("POST");

    private String desc;

    HttpMethodType(String desc) {
        this.desc = desc;
    }

    public static HttpMethodType getType(String method) {
        for (HttpMethodType mediaType : HttpMethodType.values()) {
            if (StringUtils.equals(mediaType.name(), method)) {
                return mediaType;
            }
        }
        return null;
    }
}
