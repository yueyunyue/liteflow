package cn.lite.flow.console.web.interceptor;

import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.common.utils.Constants;
import cn.lite.flow.common.utils.CookieUtils;
import cn.lite.flow.console.common.utils.RedisUtils;
import cn.lite.flow.console.common.utils.SessionContext;
import cn.lite.flow.common.utils.IpUtils;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.annotation.LoginCheckIgnore;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 登录拦截器
 */
public class ConsoleInterceptor implements HandlerInterceptor {

    private static Logger LOG = LoggerFactory.getLogger(ConsoleInterceptor.class);

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //不是方法 直接返回
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginCheckIgnore loginCheckIgnore = handlerMethod.getMethod().getAnnotation(LoginCheckIgnore.class);
        if (loginCheckIgnore != null) {
            return true;
        }

        Long userId = CookieUtils.getUserId(request);
        if (userId == null || userId < 1) {
            ResponseUtils.notLogin(request, response);
            return false;
        }

        if (redisUtils.hasKey(Constants.LITE_FLOW_USER_LOGIN_PREFIX + userId)) {
            /**延长key的过期时间*/
            redisUtils.expire(Constants.LITE_FLOW_USER_LOGIN_PREFIX + userId, Constants.LITE_FLOW_USER_LOGIN_EXPIRE_TIME, TimeUnit.SECONDS);
            CookieUtils.writeUserCookie(userId, response, Constants.LITE_FLOW_COOKIE_EXPIRE_TIME);
            String userInfo = redisUtils.get(Constants.LITE_FLOW_USER_INFO_PREFIX + userId);
            if (StringUtils.isNotEmpty(userInfo)) {
                SessionContext.setUser(JSON.parseObject(userInfo, SessionUser.class));
            }
        }

        ResponseUtils.notLogin(request, response);

        SessionUser sessionUser = SessionContext.getUser();
        if (sessionUser == null) {
            return true;
        }
        /**
         * 验证权限
         * 超级管理员有所有的权限
         */
        if (sessionUser.getIsSuper()) {
            return true;
        }

        if (handler instanceof HandlerMethod) {
            AuthCheckIgnore classAurhCheckIgnore = handlerMethod.getBeanType().getAnnotation(AuthCheckIgnore.class);
            AuthCheckIgnore authCheckIgnore = handlerMethod.getMethod().getAnnotation(AuthCheckIgnore.class);
            if (classAurhCheckIgnore != null || authCheckIgnore != null) {
                return true;
            }

            List<String> roleUrlList = sessionUser.getRoleUrls();
            if (CollectionUtils.isEmpty(roleUrlList)) {
                ResponseUtils.noAuth(request, response);
                return false;
            }

            /**
             * 校验权限
             */
            String requestURI = request.getRequestURI();
            for (String authUrl : roleUrlList) {
                if (requestURI.startsWith(authUrl)) {
                    LOG.info("auth url:{}", requestURI);
                    return true;
                }
            }

            ResponseUtils.noAuth(request, response);
            return false;

        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        SessionContext.remove();
    }
}
