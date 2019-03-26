package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.UserGroupMapper;
import cn.lite.flow.console.model.basic.UserGroup;
import cn.lite.flow.console.model.query.UserGroupQM;
import cn.lite.flow.console.service.UserGroupMidService;
import cn.lite.flow.console.service.UserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/10/18.
 */
@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupMapper userGroupMapper;
    @Autowired
    private UserGroupMidService userGroupMidService;

    @Override
    public void add(UserGroup model) {
        userGroupMapper.insert(model);
    }

    @Override
    public UserGroup getById(long id) {
        return userGroupMapper.getById(id);
    }

    @Override
    public int update(UserGroup model) {
        return userGroupMapper.update(model);
    }

    @Override
    public int count(UserGroupQM queryModel) {
        return userGroupMapper.count(queryModel);
    }

    @Override
    public List<UserGroup> list(UserGroupQM queryModel) {
        return userGroupMapper.findList(queryModel);
    }

    @Override
    public List<UserGroup> getByUserId(Long userId) {
        return userGroupMapper.getByUserId(userId);
    }

    @Transactional("consoleTxManager")
    @Override
    public void addOrUpdate(UserGroup userGroup, String userIds) {
        if (userGroup.getId() == null) {
            this.add(userGroup);
        } else {
            this.update(userGroup);
            userGroupMidService.deleteByGroupId(userGroup.getId());
        }
        if (StringUtils.isNotBlank(userIds)) {
            String[] userIdArr = userIds.split(",");
            if (userIdArr != null && userIdArr.length > 0) {
                List<Long> userIdList = Arrays.stream(userIdArr)
                        .mapToLong(userIdStr -> Long.parseLong(userIdStr))
                        .boxed()
                        .distinct().collect(Collectors.toList());
                userGroupMidService.addBatchUserId(userGroup.getId(), userIdList);
            }
        }
    }

    @Transactional("consoleTxManager")
    @Override
    public void delete(Long id) {
        userGroupMapper.delete(id);
        /**删除相应用户组和用户的关系*/
        userGroupMidService.deleteByGroupId(id);
    }
}
