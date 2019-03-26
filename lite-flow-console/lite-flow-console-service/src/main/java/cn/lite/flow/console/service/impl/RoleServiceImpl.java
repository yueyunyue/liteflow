package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.RoleMapper;
import cn.lite.flow.console.model.basic.Role;
import cn.lite.flow.console.model.query.RoleQM;
import cn.lite.flow.console.service.RoleAuthMidService;
import cn.lite.flow.console.service.RoleService;
import cn.lite.flow.console.service.UserRoleMidService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/10/24.
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMidService userRoleMidService;
    @Autowired
    private RoleAuthMidService roleAuthMidService;


    @Override
    public void add(Role model) {
        roleMapper.insert(model);
    }

    @Override
    public Role getById(long id) {
        return roleMapper.getById(id);
    }

    @Override
    public int update(Role model) {
        return roleMapper.update(model);
    }

    @Override
    public int count(RoleQM queryModel) {
        return roleMapper.count(queryModel);
    }

    @Override
    public List<Role> list(RoleQM queryModel) {
        return roleMapper.findList(queryModel);
    }

    @Override
    public List<Role> getByUserId(Long userId) {
        return roleMapper.getByUserId(userId);
    }

    @Transactional("consoleTxManager")
    @Override
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
        /**同时要删除对应的用户-角色对应关系 和  角色-权限对应关系*/
        userRoleMidService.deleteByRoleId(id);
        roleAuthMidService.deleteByRoleId(id);
    }

    @Transactional("consoleTxManager")
    @Override
    public void addOrUpdateAuthUrls(Role role, String authUrlIds) {
        if (role.getId() == null) {
            this.add(role);
        } else {
            this.update(role);
            roleAuthMidService.deleteByRoleId(role.getId());
        }
        this.addRoleAuthIds(role.getId(), authUrlIds);
    }

    private void addRoleAuthIds(Long id, String authUrlIds) {
        String[] authUrlIdArr = StringUtils.split(authUrlIds, ",");
        if (authUrlIdArr != null && authUrlIdArr.length > 0) {
            List<Long> menuItemIdList = Arrays.stream(authUrlIdArr).
                    mapToLong(authUrlIdStr -> Long.parseLong(authUrlIdStr))
                    .boxed()
                    .distinct()
                    .collect(Collectors.toList());
            roleAuthMidService.addBatchMenuItemIds(id, menuItemIdList);
        }
    }
}
