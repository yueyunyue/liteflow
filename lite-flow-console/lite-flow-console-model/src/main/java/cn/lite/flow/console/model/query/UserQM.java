package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 系统用户查询模型
 */
@Data
@ToString(callSuper = true)
public class UserQM extends BaseQM {

    private String userName;            //用户名精确匹配

    private String password;            //密码

    private String userNameLike;        //用户名模糊查询

    private String emailLike;           //邮箱模糊查询

    private Integer status;             //状态

}
