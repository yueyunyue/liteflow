package cn.lite.flow.common.model.consts;

import lombok.Getter;

/**
 * 任务的并发性
 */
@Getter
public enum SQLType {

    MYSQL(1, "MYSQL"),

    DEFAULT_HIVE_SQL(2, "默认配置的HIVE_SQL"),

    HIVE_SQL(3, "HIVE_SQL"),

    SPARK_SQL(4, "SPARK_SQL");

    private int value;

    private String desc;

    SQLType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据id
     *
     * @return
     */
    public static SQLType getType(int value) {
        for (SQLType type : SQLType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;


    }
}
