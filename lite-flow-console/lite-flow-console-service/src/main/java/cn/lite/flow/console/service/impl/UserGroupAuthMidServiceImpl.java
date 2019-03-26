package cn.lite.flow.console.service.impl;

import cn.lite.flow.console.dao.mapper.UserGroupAuthMidMapper;
import cn.lite.flow.console.model.basic.UserGroupAuthMid;
import cn.lite.flow.console.model.query.UserGroupAuthMidQM;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
@Service
public class UserGroupAuthMidServiceImpl implements UserGroupAuthMidService {

    @Autowired
    private UserGroupAuthMidMapper userGroupAuthMidMapper;

    @Override
    public void add(UserGroupAuthMid model) {
        userGroupAuthMidMapper.insert(model);
    }

    @Override
    public UserGroupAuthMid getById(long id) {
        return userGroupAuthMidMapper.getById(id);
    }

    @Override
    public int update(UserGroupAuthMid model) {
       return userGroupAuthMidMapper.update(model);
    }

    @Override
    public int count(UserGroupAuthMidQM queryModel) {
        return userGroupAuthMidMapper.count(queryModel);
    }

    @Override
    public List<UserGroupAuthMid> list(UserGroupAuthMidQM queryModel) {
        return userGroupAuthMidMapper.findList(queryModel);
    }

    @Override
    public List<Long> getTargetId(Long userId, List<Long> groupIds, int targetType) {
        return userGroupAuthMidMapper.getTargetId(userId, groupIds, targetType);
    }

    @Override
    public void addBatch(List<UserGroupAuthMid> userGroupAuthMidList) {
        userGroupAuthMidMapper.insertBatch(userGroupAuthMidList);
    }

    @Override
    public void addAuth(Long taskId, String userAuthJson, int sourceType, int targetType) {
        if (StringUtils.isNotBlank(userAuthJson)) {
            JSONArray datas = JSON.parseArray(userAuthJson);
            if (datas != null && datas.size() > 0) {
                List<UserGroupAuthMid> userGroupAuthMids = new ArrayList<>();
                datas.forEach(obj -> {
                    Long sourceId = (Long) ((JSONObject) obj).get("sourceId");
                    Integer canEdit = (Integer) ((JSONObject) obj).get("canEdit");
                    Integer canExecute = (Integer) ((JSONObject) obj).get("canExecute");

                    UserGroupAuthMid model = new UserGroupAuthMid();
                    model.setSourceId(sourceId);
                    model.setTargetId(taskId);
                    model.setSourceType(sourceType);
                    model.setTargetType(targetType);
                    model.setHasEditAuth(canEdit);
                    model.setHasExecuteAuth(canExecute);
                    userGroupAuthMids.add(model);
                });
                this.addBatch(userGroupAuthMids);
            }
        }

    }


    @Override
    public void deleteById(Long id) {
        userGroupAuthMidMapper.deleteById(id);
    }
}
