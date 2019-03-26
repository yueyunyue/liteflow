package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.console.model.basic.MenuItem;
import cn.lite.flow.console.model.query.MenuItemQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/12/24.
 */
public interface MenuItemMapper {

    /**
     * 查询列表
     *
     * @param qm
     * @return
     */
    List<MenuItem> findList(MenuItemQM qm);

    /**
     * 查看某角色下的菜单url权限
     *
     * @param roleId
     * @return
     */
    List<MenuItem> getByRoleId(@Param("roleId") Long roleId);
}
