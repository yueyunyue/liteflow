package cn.lite.flow.console.service.auth;

import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;

import java.util.List;

/**
 * Created by ly on 2018/11/9.
 */
public interface AuthCheckService {

    AuthCheckTypeEnum supportCheckType();

    /**
     * 校验用户的权限
     *
     * @param userId            用户id
     * @param targetId          目标id
     * @param operateTypeEnum   操作类型
     * @return
     */
    boolean checkUserAuth(Long userId, Long targetId, OperateTypeEnum operateTypeEnum);

    /**
     * 校验用户组的权限
     *
     * @param userGroupIds      用户组id列表
     * @param targetId          目标id
     * @param operateTypeEnum   操作类型
     * @return
     */
    boolean checkUserGroupAuth(List<Long> userGroupIds, Long targetId, OperateTypeEnum operateTypeEnum);

}
