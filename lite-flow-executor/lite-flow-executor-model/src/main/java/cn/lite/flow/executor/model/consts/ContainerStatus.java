package cn.lite.flow.executor.model.consts;

import lombok.Getter;

/**
 * container状态枚举常量
 */
@Getter
public enum ContainerStatus {

    NEW(0, "新建"),

    RUNNING(1, "运行中"),

    SUCCESS(2, "成功"),

    FAIL(-1, "失败"),
    ;

    private int value;

    private String desc;

    ContainerStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ContainerStatus getType(int value) {
        for (ContainerStatus status : ContainerStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
