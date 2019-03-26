package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.UserGroup;
import cn.lite.flow.console.model.query.UserGroupQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserGroupMapper extends BaseMapper<UserGroup, UserGroupQM> {

    /**
     * 根据用户id查询用户组
     *
     * @param userId
     * @return
     */
    List<UserGroup> getByUserId(@Param("userId") Long userId);

    /**
     * 删除用户组
     *
     * @param id
     * @return
     */
    int delete(@Param("id") Long id);
}
