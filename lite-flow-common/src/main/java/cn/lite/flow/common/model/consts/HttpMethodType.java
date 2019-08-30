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

    /**
     * 根据id或者媒体类型
     * @param id 媒体类型id
     * @return
     */
    public static HttpMethodType getType(String id) {
        for (HttpMethodType mediaType : HttpMethodType.values()) {
            if (StringUtils.equals(mediaType.name(), id)) {
                return mediaType;
            }
        }
        return null;
    }
}
