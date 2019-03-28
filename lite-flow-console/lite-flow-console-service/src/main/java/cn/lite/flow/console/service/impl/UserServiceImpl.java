package cn.lite.flow.console.service.impl;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.common.utils.CodecUtils;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.dao.mapper.UserMapper;
import cn.lite.flow.console.model.basic.*;
import cn.lite.flow.console.model.query.MenuQM;
import cn.lite.flow.console.model.query.RoleAuthMidQM;
import cn.lite.flow.console.model.query.RoleQM;
import cn.lite.flow.console.model.query.UserQM;
import cn.lite.flow.console.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/10/18.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserGroupMidService userGroupMidService;
    @Autowired
    private UserRoleMidService userRoleMidService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleAuthMidService roleAuthMidService;

    @Override
    public void add(User model) {
        model.setPassword(CodecUtils.encodePassword(model.getPassword()));
        model.setStatus(StatusType.NEW.getValue());
        userMapper.insert(model);
    }

    @Override
    public User getById(long id) {
        return userMapper.getById(id);
    }

    @Override
    public int update(User model) {
        if (StringUtils.isNotBlank(model.getPassword())) {
            model.setPassword(CodecUtils.encodePassword(model.getPassword()));
        }
        return userMapper.update(model);
    }

    @Override
    public int count(UserQM queryModel) {
        return userMapper.count(queryModel);
    }

    @Override
    public List<User> list(UserQM queryModel) {
        return userMapper.findList(queryModel);
    }

    @Transactional("consoleTxManager")
    @Override
    public void addOrUpdate(User user, String roleIds) {
        if (user.getId() == null) {
            this.add(user);
        } else {
            this.update(user);
            userRoleMidService.deleteByUserId(user.getId());
        }
        this.addUserRoles(user.getId(), roleIds);
    }

    private void addUserRoles(Long id, String roleIds) {
        String[] roleIdArr = StringUtils.split(roleIds, ",");
        if (roleIdArr != null && roleIdArr.length > 0) {
            List<Long> roleIdList = Arrays.stream(roleIdArr)
                    .mapToLong(roleIdStr -> Long.parseLong(roleIdStr))
                    .boxed()
                    .distinct().collect(Collectors.toList());

            userRoleMidService.addBatchRoleId(id, roleIdList);
        }
    }

    @Override
    public SessionUser checkLogin(String userName, String password) {
        UserQM userQM = new UserQM();
        userQM.setUserName(userName);
        userQM.setPassword(CodecUtils.encodePassword(password));
        List<User> userList = this.list(userQM);

        if (CollectionUtils.isNotEmpty(userList)) {
            User user = userList.get(0);
            if (!user.getStatus().equals(StatusType.ON.getValue())) {
                return null;
            }
            SessionUser sessionUser = new SessionUser();
            sessionUser.setId(user.getId());
            sessionUser.setUserName(userName);
            sessionUser.setIsSuper(user.getIsSuper() == 1);

            /**查询用户所属的用户组*/
            List<UserGroup> userGroupList = userGroupService.getByUserId(user.getId());
            if (CollectionUtils.isNotEmpty(userGroupList)) {
                List<Long> userGroupIdList = userGroupList.stream()
                        .map(UserGroup::getId)
                        .distinct()
                        .collect(Collectors.toList());
                sessionUser.setGroupIds(userGroupIdList);
            }

            /**查询用户角色*/
            Set<Long> menuItemIdSet = null;
            boolean isSuper = sessionUser.getIsSuper();
            if (!isSuper) {
                List<Role> roleList = roleService.getByUserId(user.getId());
                if (CollectionUtils.isNotEmpty(roleList)) {
                    List<Long> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toList());

                    /**获取角色下的菜单权限*/
                    RoleAuthMidQM roleAuthMidQM = new RoleAuthMidQM();
                    roleAuthMidQM.setRoleIds(roleIds);
                    List<RoleAuthMid> roleAuthMidList = roleAuthMidService.list(roleAuthMidQM);
                    if (CollectionUtils.isNotEmpty(roleAuthMidList)) {
                        menuItemIdSet = roleAuthMidList.stream()
                                .map(RoleAuthMid::getMenuItemId)
                                .distinct()
                                .collect(Collectors.toSet());
                    }

                }
            }
            /**查询菜单*/
            MenuQM menuQM = new MenuQM();
            menuQM.addOrderAsc(MenuQM.COL_ORDER_NUM);
            List<Menu> menuAndItems = menuService.listMenuAndItems(menuQM);
            List<Menu> menus = new ArrayList<>();
            List<String> roleUrlList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(menuAndItems)) {
                for(Menu menu :menuAndItems) {
                    List<MenuItem> menuItems = menu.getMenuItemList();
                    if (CollectionUtils.isNotEmpty(menuItems)) {
                        List<MenuItem> items = new ArrayList<>();
                        for(MenuItem item : menuItems){
                            boolean isAddData = false;
                            if(isSuper){
                                isAddData = true;
                            }else if (CollectionUtils.isNotEmpty(menuItemIdSet) && menuItemIdSet.contains(item.getId())) {
                                isAddData = true;

                            }
                            if(isAddData){
                                items.add(item);
                                roleUrlList.add(item.getUrl());
                            }
                        }
                        if (CollectionUtils.isNotEmpty(items)) {
                            menu.setMenuItemList(items);
                            menus.add(menu);
                        }
                    }
                }
            }
            sessionUser.setMenus(menus);
            sessionUser.setRoleUrls(roleUrlList);

            return sessionUser;

        }
        return null;
    }

    @Override
    public List<User> getByGroupId(Long groupId) {
        return userMapper.getByGroupId(groupId);
    }
}
