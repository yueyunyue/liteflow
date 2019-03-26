package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.UserRoleMid;
import cn.lite.flow.console.model.query.UserRoleMidQM;

import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
public interface UserRoleMidService extends BaseService<UserRoleMid, UserRoleMidQM> {

    /**
     * 删除指定用户角色
     *
     * @param userId    用户id
     */
    void deleteByUserId(Long userId);

    /**
     * 删除指定角色的相关记录
     *
     * @param roleId    角色id
     */
    void deleteByRoleId(Long roleId);

    /**
     * 批量添加
     *
     * @param userRoleMidList
     */
    void addBatch(List<UserRoleMid> userRoleMidList);

    /**
     * 批量给用户添加角色
     *
     * @param userId        用户id
     * @param roleIdList    角色id列表
     */
    void addBatchRoleId(Long userId, List<Long> roleIdList);
}
