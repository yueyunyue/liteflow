package cn.lite.flow.console.service;

import cn.lite.flow.console.model.basic.Menu;
import cn.lite.flow.console.model.query.MenuQM;

import java.util.List;

/**
 * Created by ly on 2018/12/24.
 */
public interface MenuService {

    /**
     * 查询菜单以及对应的列表项
     *
     * @param qm
     * @return
     */
    List<Menu> listMenuAndItems(MenuQM qm);
}
