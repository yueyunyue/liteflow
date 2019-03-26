package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.RoleAuthMid;
import cn.lite.flow.console.model.query.RoleAuthMidQM;

import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
public interface RoleAuthMidService extends BaseService<RoleAuthMid, RoleAuthMidQM> {

    /**
     * 根据角色id 删除相关记录
     *
     * @param roleId    角色id
     */
    void deleteByRoleId(Long roleId);

    /**RoleAuthMid
     * 批量添加
     *
     * @param roleAuthMidList
     */
    void addBatch(List<RoleAuthMid> roleAuthMidList);

    /**
     * 批量添加某个角色的权限
     *
     * @param roleId            角色id
     * @param menuItemIds     权限id列表
     */
    void addBatchMenuItemIds(Long roleId, List<Long> menuItemIds);
}
