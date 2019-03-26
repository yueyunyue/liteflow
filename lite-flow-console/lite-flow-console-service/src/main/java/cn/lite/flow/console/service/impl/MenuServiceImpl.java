package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.MenuItemMapper;
import cn.lite.flow.console.dao.mapper.MenuMapper;
import cn.lite.flow.console.model.basic.Menu;
import cn.lite.flow.console.model.query.MenuItemQM;
import cn.lite.flow.console.model.query.MenuQM;
import cn.lite.flow.console.service.MenuService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;


/**
 * Created by ly on 2018/12/24.
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private MenuItemMapper menuItemMapper;

    @Override
    public List<Menu> listMenuAndItems(MenuQM qm) {
        List<Menu> menuList = menuMapper.findList(qm);
        if (CollectionUtils.isNotEmpty(menuList)) {
            menuList.forEach(o -> {
                MenuItemQM menuItemQM = new MenuItemQM();
                menuItemQM.setMenuId(o.getId());
                menuItemQM.addOrderAsc(MenuItemQM.COL_ORDER_NUM);
                o.setMenuItemList(menuItemMapper.findList(menuItemQM));
            });
        }
        return menuList;
    }
}
