package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * Created by ly on 2018/12/24.
 */
@Data
@ToString
public class MenuQM extends BaseQM {

    private String name;        //按名称精确匹配

    public static final String COL_ORDER_NUM = "order_num";
}
