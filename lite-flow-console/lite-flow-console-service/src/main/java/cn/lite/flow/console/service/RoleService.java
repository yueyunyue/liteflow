package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.Role;
import cn.lite.flow.console.model.query.RoleQM;

import java.util.List;

/**
 * Created by ly on 2018/10/24.
 */
public interface RoleService extends BaseService<Role, RoleQM> {

    /**
     * 查找用户的角色
     *
     * @param userId        用户id
     * @return
     */
    List<Role> getByUserId(Long userId);

    /**
     * 根据id删除对应的记录
     *
     * @param id        角色id
     */
    void deleteById(Long id);

    /**
     * 添加用户角色和权限
     *
     * @param role          角色
     * @param authUrlIds    权限urlId列表
     */
    void addOrUpdateAuthUrls(Role role, String authUrlIds);


}
