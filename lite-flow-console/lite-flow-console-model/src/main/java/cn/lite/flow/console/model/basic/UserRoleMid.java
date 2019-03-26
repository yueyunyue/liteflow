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
public class UserRoleMid implements Serializable {

    private Long id;

    private Long userId;            //用户id

    private Long roleId;            //角色id

    private Date createTime;        //创建时间
}
