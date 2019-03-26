package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.UserGroupAuthMid;
import cn.lite.flow.console.model.query.UserGroupAuthMidQM;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserGroupAuthMidService extends BaseService<UserGroupAuthMid, UserGroupAuthMidQM> {

    /**
     * 查找用户和用户组所拥有的所有权限
     *
     * @param userId        用户id
     * @param groupIds      用户组id列表
     * @param targetType    目标类型       参考TargetTypeEnum
     * @return
     */
    List<Long> getTargetId(Long userId, List<Long> groupIds, int targetType);

    /**
     * 批量插入
     *
     * @param userGroupAuthMidList
     */
    void addBatch(List<UserGroupAuthMid> userGroupAuthMidList);

    /**
     * 批量添加权限       如果存在重复的，则忽略
     *
     * @param taskId            任务id
     * @param userAuthJson      权限json串  [{"sourceId": 1,"hasEditAuth": 1, "hasExecuteAuth": 0},{"sourceId": 2,"hasEditAuth": 0, "hasExecuteAuth": 1}]
     * @param sourceType        源id类型 参考SourceTypeEnum
     * @param targetId          目标id类型  参考TargetTypeEnum
     */
    void addAuth(Long taskId, String userAuthJson, int sourceType, int targetId);

    /**
     * 删除
     *
     * @param id
     */
    void deleteById(Long id);

}
