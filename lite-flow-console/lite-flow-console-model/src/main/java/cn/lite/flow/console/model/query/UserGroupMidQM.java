package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 系统用户和用户组对应关系查询模型
 */
@Data
@ToString(callSuper = true)
public class UserGroupMidQM extends BaseQM {

    private Long userId;        //用户id

    private Long groupId;       //用户组id

}
