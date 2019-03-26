package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * Created by ly on 2018/12/24.
 */
@Data
@ToString
public class MenuItemQM extends BaseQM {

    private Long menuId;            //菜单id

    public static final String COL_ORDER_NUM = "order_num";
}
