package cn.lite.flow.executor.model.consts;

import lombok.Getter;

/**
 * 附件相关类型
 */
@Getter
public enum ExecutorAttachmentType {

    TXT(1, "txt文本");

    private int value;

    private String desc;

    ExecutorAttachmentType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    public static ExecutorAttachmentType getType(int value) {
        for (ExecutorAttachmentType status : ExecutorAttachmentType.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
