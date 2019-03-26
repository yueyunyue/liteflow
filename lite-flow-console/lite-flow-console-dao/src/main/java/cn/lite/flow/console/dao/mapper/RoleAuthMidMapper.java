package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.RoleAuthMid;
import cn.lite.flow.console.model.query.RoleAuthMidQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/31.
 */
public interface RoleAuthMidMapper extends BaseMapper<RoleAuthMid, RoleAuthMidQM> {

    /**
     * 根据角色id 删除相应的记录
     *
     * @param roleId    角色id
     * @return
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限id 删除相应的记录
     *
     * @param authUrlId     权限id
     * @return
     */
    int deleteByAuthUrlId(@Param("authUrlId") Long authUrlId);

    /**
     * 批量添加
     *
     * @param roleAuthMidList
     * @return
     */
    int insertBatch(List<RoleAuthMid> roleAuthMidList);
}
