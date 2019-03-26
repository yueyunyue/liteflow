package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.UserRoleMid;
import cn.lite.flow.console.model.query.UserRoleMidQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
public interface UserRoleMidMapper extends BaseMapper<UserRoleMid, UserRoleMidQM> {

    /**
     * 删除用户的角色
     *
     * @param userId    用户id
     * @return
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除指定角色的关系
     *
     * @param roleId    角色id
     * @return
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量添加
     *
     * @param userRoleMidList
     * @return
     */
    int insertBatch(List<UserRoleMid> userRoleMidList);
}
