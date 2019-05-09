package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * Created by ly on 2018/10/24.
 */
@Data
@ToString(callSuper = true)
public class RoleQM extends BaseQM {

    private String roleNameLike;        //角色名 模糊查询

    private String roleName;            //角色名 精确匹配

}
