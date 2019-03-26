package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.MenuItemMapper;
import cn.lite.flow.console.model.basic.MenuItem;
import cn.lite.flow.console.model.query.MenuItemQM;
import cn.lite.flow.console.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ly on 2018/12/27.
 */
@Service
public class MenuItemServiceImpl implements MenuItemService {

    @Autowired
    private MenuItemMapper menuItemMapper;

    @Override
    public List<MenuItem> list(MenuItemQM menuItemQM) {
        return menuItemMapper.findList(menuItemQM);
    }

    @Override
    public List<MenuItem> getByRoleId(Long roleId) {
        return menuItemMapper.getByRoleId(roleId);
    }
}
