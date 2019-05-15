package cn.lite.flow.common.model.consts;

import lombok.Getter;

/**
 * 时间枚举
 */
@Getter
public enum TimeUnit {

    SECOND(1, "s", "秒", "yyyyMMddHHmmss"),

    MINUTE(2, "m", "分", "yyyyMMddHHmm"),

    HOUR(3, "h", "时", "yyyyMMddHH"),

    DAY(4, "d", "日", "yyyyMMdd"),

    WEEK(5, "w", "周", "yyyyMMdd"),

    MONTH(6, "M", "月", "yyyyMMdd"),

    YEAR(7, "y", "年", "yyyyMMdd");

    private int value;

    private String suffix;

    private String desc;

    private String versionExpression;

    TimeUnit(int value, String suffix, String desc, String versionExpression) {
        this.value = value;
        this.suffix = suffix;
        this.desc = desc;
        this.versionExpression = versionExpression;
    }

    /**
     * 根据id
     * @return
     */
    public static TimeUnit getType(int id) {
        for (TimeUnit mediaType : TimeUnit.values()) {
            if (mediaType.getValue() == id) {
                return mediaType;
            }
        }
        return null;
    }

    /**
     * 根据value查询desc
     *
     * @param value     要查询的value值
     * @return
     */
    public static String findDescByValue(int value) {
        for (TimeUnit timeUnit : TimeUnit.values()) {
            if (timeUnit.getValue() == value) {
                return timeUnit.getDesc();
            }
        }
        return "";
    }
}
