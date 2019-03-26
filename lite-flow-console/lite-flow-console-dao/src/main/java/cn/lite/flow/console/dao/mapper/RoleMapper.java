package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.Role;
import cn.lite.flow.console.model.query.RoleQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/24.
 */
public interface RoleMapper extends BaseMapper<Role, RoleQM> {

    /**
     * 查找用户的角色
     *
     * @param userId        用户id
     * @return
     */
    List<Role> getByUserId(@Param("userId") Long userId);

    /**
     * 根据id删除对应的记录
     *
     * @param id            角色id
     * @return
     */
    int deleteById(@Param("id") Long id);
}
