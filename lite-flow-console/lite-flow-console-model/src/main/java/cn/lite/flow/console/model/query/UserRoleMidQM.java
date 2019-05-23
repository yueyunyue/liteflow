package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * Created by ly on 2018/10/24.
 */
@Data
@ToString(callSuper = true)
public class UserRoleMidQM extends BaseQM {

    private Long userId;        //用户id
}
