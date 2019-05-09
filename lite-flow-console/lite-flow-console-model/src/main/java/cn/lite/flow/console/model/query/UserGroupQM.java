package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 系统用户组查询模型
 */
@Data
@ToString(callSuper = true)
public class UserGroupQM extends BaseQM {

    private String name;                    //用户组名精确匹配

    private String nameLike;                //用户组名模糊查询

}
