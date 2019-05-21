package cn.lite.flow.executor.model.consts;

import lombok.Getter;

/**
 * container日志类型
 */
@Getter
public enum ContainerLogType {

    LOCAL(1, "本地"),

    YARN(2, "yarn"),

    ;

    private int value;

    private String desc;

    ContainerLogType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ContainerLogType getType(int value) {
        for (ContainerLogType status : ContainerLogType.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
