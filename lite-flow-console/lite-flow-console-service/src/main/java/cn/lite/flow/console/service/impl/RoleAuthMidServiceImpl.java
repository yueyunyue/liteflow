package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.RoleAuthMidMapper;
import cn.lite.flow.console.model.basic.RoleAuthMid;
import cn.lite.flow.console.model.query.RoleAuthMidQM;
import cn.lite.flow.console.service.RoleAuthMidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
@Service
public class RoleAuthMidServiceImpl implements RoleAuthMidService {

    @Autowired
    private RoleAuthMidMapper roleAuthMidMapper;

    @Override
    public void add(RoleAuthMid model) {
        roleAuthMidMapper.insert(model);
    }

    @Override
    public RoleAuthMid getById(long id) {
        return roleAuthMidMapper.getById(id);
    }

    @Override
    public int update(RoleAuthMid model) {
        throw new IllegalStateException("no method");
    }

    @Override
    public int count(RoleAuthMidQM queryModel) {
        return roleAuthMidMapper.count(queryModel);
    }

    @Override
    public List<RoleAuthMid> list(RoleAuthMidQM queryModel) {
        return roleAuthMidMapper.findList(queryModel);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleAuthMidMapper.deleteByRoleId(roleId);
    }

    @Override
    public void addBatch(List<RoleAuthMid> roleAuthMidList) {
        roleAuthMidMapper.insertBatch(roleAuthMidList);
    }

    @Override
    public void addBatchMenuItemIds(Long roleId, List<Long> menuItemIdList) {
        List<RoleAuthMid> roleAuthMidList = new ArrayList<>();
        menuItemIdList.forEach(menuItemId -> {
            RoleAuthMid roleAuthMid = new RoleAuthMid();
            roleAuthMid.setRoleId(roleId);
            roleAuthMid.setMenuItemId(menuItemId);
            roleAuthMidList.add(roleAuthMid);
        });
        this.addBatch(roleAuthMidList);
    }
}
