package cn.lite.flow.common.model.consts;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 状态枚举常量
 */
@Getter
public enum FileType {

    LOCAL(0, "","本地"),

    HDFS(1, CommonConstants.HDFS_PREFIX, "hdfs"),

    LITE_ATTACHMENT(2, CommonConstants.ATTACHMENT_PREFIX, "基于db的附件");

    private int value;

    private String prefix;

    private String desc;

    FileType(int value, String prefix, String desc) {
        this.prefix = prefix;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据id或者媒体类型
     * @param id 媒体类型id
     * @return
     */
    public static FileType getType(int id) {
        for (FileType mediaType : FileType.values()) {
            if (mediaType.getValue() == id) {
                return mediaType;
            }
        }
        return null;
    }

    /**
     * 根据文件路径判断文件类型
     * @param path
     * @return
     */
    public static FileType getTypeByFilePatch(String path){

        if(StringUtils.isBlank(path)){
            return null;
        }
        if(StringUtils.startsWith(path, HDFS.getPrefix())){
            return HDFS;
        }
        if(StringUtils.startsWith(path, LITE_ATTACHMENT.getPrefix())){
            return LITE_ATTACHMENT;
        }
        return LOCAL;

    }
}
