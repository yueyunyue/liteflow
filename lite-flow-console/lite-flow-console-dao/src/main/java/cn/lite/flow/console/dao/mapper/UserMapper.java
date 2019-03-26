package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.User;
import cn.lite.flow.console.model.query.UserQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserMapper extends BaseMapper<User, UserQM> {

    /**
     * 查找用户组下的用户信息
     *
     * @param groupId   用户组id
     * @return
     */
    List<User> getByGroupId(@Param("groupId") Long groupId);
}
