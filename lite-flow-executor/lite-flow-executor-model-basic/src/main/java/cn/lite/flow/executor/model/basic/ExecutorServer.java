package cn.lite.flow.executor.model.basic;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ly on 2018/12/14.
 */
@Data
@ToString
public class ExecutorServer implements Serializable {

    private Long id;                   //id

    private String name;               //名称

    private String ip;                 //ip地址

    private Integer status;            //状态

    private String description;        //说明

    private Long userId;               //创建者id

    private Date createTime;           //创建时间

    private Date updateTime;           //更新时间
}
