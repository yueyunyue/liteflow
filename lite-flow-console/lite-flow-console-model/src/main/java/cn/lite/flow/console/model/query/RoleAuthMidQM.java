package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
@Data
@ToString
public class RoleAuthMidQM extends BaseQM {

    private Long roleId;            //角色id

    private List<Long> roleIds;     //角色id列表

    private Long menuItemId;        //子菜单 id
}
