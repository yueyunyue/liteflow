package cn.lite.flow.console.service.test;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.model.basic.User;
import cn.lite.flow.console.service.UserService;
import cn.lite.flow.console.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ly on 2018/12/21.
 */
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void addTest() {
        User user = new User();
        user.setUserName("admin");
        user.setPassword("admin123");
        user.setEmail("");
        user.setPhone("13166668888");
        user.setIsSuper(1);
        userService.add(user);
    }

    @Test
    public void getByIdTest() {
        User user = userService.getById(1L);
        if (user != null) {

        }
    }

    @Test
    public void updateTest() {
        User user = new User();
        user.setId(1L);
        user.setStatus(StatusType.ON.getValue());
        userService.update(user);
    }


}
