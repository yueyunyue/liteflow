package cn.lite.flow.console.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.UserGroup;
import cn.lite.flow.console.model.query.UserGroupQM;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserGroupService extends BaseService<UserGroup, UserGroupQM> {

    /**
     * 查找用户所属的用户组
     *
     * @param userId    用户id
     * @return
     */
    List<UserGroup> getByUserId(Long userId);

    /**
     * 添加 or 更新用户组
     *
     * @param userGroup     用户组
     * @param userIds       用户id字符串
     */
    void addOrUpdate(UserGroup userGroup, String userIds);

    /**
     * 删除用户组
     *
     * @param id
     */
    void delete(Long id);
}
