package cn.lite.flow.common.model.consts;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * http code枚举常量
 */
@Getter
public enum HttpCodeType {

    CODE_200(200, "200"),
    ;

    private int code;

    private String desc;

    HttpCodeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据id或者媒体类型
     * @param code 媒体类型id
     * @return
     */
    public static HttpCodeType getType(int code) {
        for (HttpCodeType mediaType : HttpCodeType.values()) {
            if (mediaType.getCode() == code) {
                return mediaType;
            }
        }
        return null;
    }
}
