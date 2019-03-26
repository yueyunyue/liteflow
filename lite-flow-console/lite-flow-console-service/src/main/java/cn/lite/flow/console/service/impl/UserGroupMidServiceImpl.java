package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.dao.mapper.UserGroupMidMapper;
import cn.lite.flow.console.model.basic.UserGroupMid;
import cn.lite.flow.console.model.query.UserGroupMidQM;
import cn.lite.flow.console.service.UserGroupMidService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
@Service
public class UserGroupMidServiceImpl implements UserGroupMidService {

    @Autowired
    private UserGroupMidMapper userGroupMidMapper;

    @Override
    public void add(UserGroupMid model) {
        userGroupMidMapper.insert(model);
    }

    @Override
    public UserGroupMid getById(long id) {
        return userGroupMidMapper.getById(id);
    }

    @Override
    public int update(UserGroupMid model) {
        throw new ConsoleRuntimeException("非法操作");
    }

    @Override
    public int count(UserGroupMidQM queryModel) {
        return userGroupMidMapper.count(queryModel);
    }

    @Override
    public List<UserGroupMid> list(UserGroupMidQM queryModel) {
        return userGroupMidMapper.findList(queryModel);
    }

    @Override
    public void addBatch(List<UserGroupMid> userGroupMidList) {
        userGroupMidMapper.insertBatch(userGroupMidList);
    }

    @Override
    public void deleteByGroupId(Long groupId) {
        userGroupMidMapper.deleteByGroupId(groupId);
    }

    @Override
    public void addBatchUserId(Long groupId, List<Long> userIdList) {
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<UserGroupMid> userGroupMidList = new ArrayList<>();
            userIdList.forEach(userId -> {
                UserGroupMid usergroupMid = new UserGroupMid();
                usergroupMid.setUserId(userId);
                usergroupMid.setGroupId(groupId);
                userGroupMidList.add(usergroupMid);
            });
            this.addBatch(userGroupMidList);
        }
    }
}
