package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.UserGroupAuthMid;
import cn.lite.flow.console.model.query.UserGroupAuthMidQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserGroupAuthMidMapper extends BaseMapper<UserGroupAuthMid, UserGroupAuthMidQM> {

    /**
     * 查找用户和用户组所拥有的所有权限
     *
     * @param userId        用户id
     * @param groupIds      用户组id
     * @param targetType    目标类型    参考TargetTypeEnum
     * @return
     */
    List<Long> getTargetId(@Param("userId") Long userId, @Param("groupIds") List<Long> groupIds, @Param("targetType") int targetType);

    /**
     * 批量添加
     *
     * @param userGroupAuthMidList
     * @return
     */
    int insertBatch(List<UserGroupAuthMid> userGroupAuthMidList);

    /**
     * 删除目标权限
     *
     * @param targetId          目标id
     * @param type              权限类型
     * @return
     */
    int deleteTargetAuth(@Param("targetId") Long targetId, @Param("type") int type);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    int deleteById(@Param("id") Long id);
}
