package cn.lite.flow.console.service;

import cn.lite.flow.console.model.basic.MenuItem;
import cn.lite.flow.console.model.query.MenuItemQM;

import java.util.List;

/**
 * Created by ly on 2018/12/27.
 */
public interface MenuItemService {

    /**
     * 查询列表
     *
     * @param menuItemQM
     * @return
     */
    List<MenuItem> list(MenuItemQM menuItemQM);

    /**
     * 查询某角色下的菜单权限列表
     *
     * @param roleId
     * @return
     */
    List<MenuItem> getByRoleId(Long roleId);
}
