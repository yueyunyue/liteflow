package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.UserRoleMidMapper;
import cn.lite.flow.console.model.basic.UserRoleMid;
import cn.lite.flow.console.model.query.UserRoleMidQM;
import cn.lite.flow.console.service.UserRoleMidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
@Service
public class UserRoleMidServiceImpl implements UserRoleMidService {

    @Autowired
    private UserRoleMidMapper userRoleMidMapper;

    @Override
    public void add(UserRoleMid model) {
        userRoleMidMapper.insert(model);
    }

    @Override
    public UserRoleMid getById(long id) {
        return userRoleMidMapper.getById(id);
    }

    @Override
    public int update(UserRoleMid model) {
        throw new IllegalStateException("no method");
    }

    @Override
    public int count(UserRoleMidQM queryModel) {
        return userRoleMidMapper.count(queryModel);
    }

    @Override
    public List<UserRoleMid> list(UserRoleMidQM queryModel) {
        return userRoleMidMapper.findList(queryModel);
    }

    public void deleteByUserId(Long userId) {
        userRoleMidMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        userRoleMidMapper.deleteByRoleId(roleId);
    }

    @Override
    public void addBatch(List<UserRoleMid> userRoleMidList) {
        userRoleMidMapper.insertBatch(userRoleMidList);
    }

    @Override
    public void addBatchRoleId(Long userId, List<Long> roleIdList) {
        List<UserRoleMid> userRoleMidList = new ArrayList<>();
        roleIdList.forEach(roleId -> {
            UserRoleMid userRoleMid = new UserRoleMid();
            userRoleMid.setUserId(userId);
            userRoleMid.setRoleId(roleId);
            userRoleMidList.add(userRoleMid);
        });
        this.addBatch(userRoleMidList);
    }
}
