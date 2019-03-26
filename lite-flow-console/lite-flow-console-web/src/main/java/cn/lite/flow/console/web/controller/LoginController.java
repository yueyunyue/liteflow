package cn.lite.flow.console.web.controller;

import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.common.utils.Constants;
import cn.lite.flow.common.utils.CookieUtils;
import cn.lite.flow.console.common.utils.RedisUtils;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.Menu;
import cn.lite.flow.console.model.basic.MenuItem;
import cn.lite.flow.console.service.UserService;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.annotation.LoginCheckIgnore;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ly on 2018/10/31.
 */
@RestController
@RequestMapping("console")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping(value = "login")
    @LoginCheckIgnore
    public String login(
            @RequestParam(value = "username") String userName,
            @RequestParam(value = "password") String password,
            HttpServletResponse response
    ) {
        SessionUser sessionUser = userService.checkLogin(userName, password);
        if (sessionUser == null) {
            return ResponseUtils.error("帐号或密码错误");
        }

        redisUtils.set(Constants.LITE_FLOW_USER_LOGIN_PREFIX + sessionUser.getId(), sessionUser.getId().toString(),
                Constants.LITE_FLOW_USER_LOGIN_EXPIRE_TIME, TimeUnit.SECONDS);
        /**缓存用户信息*/
        redisUtils.set(Constants.LITE_FLOW_USER_INFO_PREFIX + sessionUser.getId(), JSON.toJSONString(sessionUser),
                Constants.LITE_FLOW_USER_LOGIN_EXPIRE_TIME, TimeUnit.SECONDS);
        CookieUtils.writeUserCookie(sessionUser.getId(), response, Constants.LITE_FLOW_COOKIE_EXPIRE_TIME);

        JSONObject data = new JSONObject();
        data.put("id", sessionUser.getId());
        data.put("userName", sessionUser.getUserName());
        data.put("isSuper", sessionUser.getIsSuper());

        return ResponseUtils.success(data);
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    @AuthCheckIgnore
    @RequestMapping(value = "logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Long userId = CookieUtils.getUserId(request);
        SessionUser sessionUser = getUser();
        if (userId != null) {
            CookieUtils.removeUserCookie(response);
            redisUtils.delete(Constants.LITE_FLOW_USER_LOGIN_PREFIX + sessionUser.getId());
            redisUtils.delete(Constants.LITE_FLOW_USER_INFO_PREFIX + sessionUser.getId());
        }
        return ResponseUtils.success("退出登录成功");
    }

    /**
     * 获取用户登录信息
     * @return
     */
    @AuthCheckIgnore
    @RequestMapping(value = "userInfo")
    public String info() {
        SessionUser user = getUser();
        if(user == null){
            return ResponseUtils.notLogin();
        }
        List<Menu> menus = user.getMenus();
        JSONArray menuDatas = new JSONArray();
        if (CollectionUtils.isNotEmpty(menus)) {
            menus.forEach(menu -> {
                JSONObject obj = new JSONObject();
                obj.put("key", menu.getId().toString());
                obj.put("name", menu.getName());
                obj.put("icon", menu.getIcon());
                obj.put("order", menu.getOrderNum());

                List<MenuItem> menuItems = menu.getMenuItemList();
                JSONArray itemDatas = new JSONArray();
                if (CollectionUtils.isNotEmpty(menuItems)) {
                    if (menuItems.size() == 1) {
                        obj.put("url", menuItems.get(0).getUrl());
                    } else {
                        menuItems.forEach(o -> {
                            JSONObject item = new JSONObject();
                            item.put("key", "menuItem" + o.getId());
                            item.put("name", o.getName());
                            item.put("order", o.getOrderNum());
                            item.put("url", o.getUrl());
                            itemDatas.add(item);
                        });
                    }
                }
                obj.put("children", itemDatas);
                menuDatas.add(obj);
            });
        }
        JSONObject obj = new JSONObject();
        obj.put("id", user.getId());
        obj.put("name", user.getUserName());
        obj.put("isSuper", user.getIsSuper());
        obj.put("menus", menuDatas);
        return ResponseUtils.success(obj);
    }

}
