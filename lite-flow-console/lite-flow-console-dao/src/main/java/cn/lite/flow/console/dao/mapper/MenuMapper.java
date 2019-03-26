package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.console.model.basic.Menu;
import cn.lite.flow.console.model.query.MenuQM;

import java.util.List;

/**
 * Created by ly on 2018/12/24.
 */
public interface MenuMapper {

    List<Menu> findList(MenuQM menuQM);
}
