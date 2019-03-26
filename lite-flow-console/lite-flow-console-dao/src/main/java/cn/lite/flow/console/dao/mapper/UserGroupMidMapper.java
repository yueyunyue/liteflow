package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.UserGroupMid;
import cn.lite.flow.console.model.query.UserGroupMidQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserGroupMidMapper extends BaseMapper<UserGroupMid, UserGroupMidQM> {

    /**
     * 批量添加
     *
     * @param userGroupMidList
     * @return
     */
    int insertBatch(List<UserGroupMid> userGroupMidList);

    /**
     * 通过用户组id删除
     *
     * @param groupId   用户组id
     * @return
     */
    int deleteByGroupId(@Param("groupId") Long groupId);
}
