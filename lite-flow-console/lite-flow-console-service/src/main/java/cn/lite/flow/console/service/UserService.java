package cn.lite.flow.console.service;

import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.User;
import cn.lite.flow.console.model.query.UserQM;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
public interface UserService extends BaseService<User, UserQM> {

    /**
     * 添加 or 更新用户
     *
     * @param user          用户
     * @param roleIds       权限id字符串     以英文半角逗号分隔
     */
    void addOrUpdate(User user, String roleIds);

    /**
     * 登录
     *
     * @param userName      用户名
     * @param password      密码
     * @return
     */
    SessionUser checkLogin(String userName, String password);

    /**
     * 查询某一个用户组下的所有用户
     *
     * @param groupId
     * @return
     */
    List<User> getByGroupId(Long groupId);
}
