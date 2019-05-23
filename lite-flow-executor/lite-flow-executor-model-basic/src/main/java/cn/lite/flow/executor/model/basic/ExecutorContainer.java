package cn.lite.flow.executor.model.basic;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 容器
 */
@Data
@ToString
public class ExecutorContainer implements Serializable {

    private Long id;

    private String name;                //容器名称

    private String fieldConfig;         //插件需要实现的参数

    private String className;           //类名

    private String description;         //容器描述

    private Long userId;                //创建者id

    private Integer status;             //容器状态

    private Integer logType;            //容器状态

    private Date createTime;            //创建时间

    private Date updateTime;            //更新时间

}
