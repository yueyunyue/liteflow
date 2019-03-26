package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.UserGroupMid;
import cn.lite.flow.console.model.query.UserGroupMidQM;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserGroupMidService extends BaseService<UserGroupMid, UserGroupMidQM> {

    /**
     * 批量添加
     *
     * @param userGroupMidList
     */
    void addBatch(List<UserGroupMid> userGroupMidList);

    /**
     * 通过用户组id删除
     *
     * @param groupId    用户组id
     */
    void deleteByGroupId(Long groupId);

    /**
     * 给用户组批量添加用户
     *
     * @param groupId       用户组id
     * @param userIdList    用户id列表
     */
    void addBatchUserId(Long groupId, List<Long> userIdList);
}
