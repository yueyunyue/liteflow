package cn.lite.flow.console.model.basic;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ly on 2018/10/19.
 */
@Data
@ToString
public class Role implements Serializable {

    private Long id;

    private String roleName;            //角色名

    private String description;         //说明

    private Long userId;                //创建者id

    private Date createTime;            //创建时间

    private Date updateTime;            //更新时间

}
