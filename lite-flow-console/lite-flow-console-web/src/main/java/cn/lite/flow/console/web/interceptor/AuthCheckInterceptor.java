package cn.lite.flow.console.web.interceptor;

import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.utils.SessionContext;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.service.auth.AuthCheckManager;
import cn.lite.flow.console.web.annotation.AuthCheck;
import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ly on 2018/11/5.
 */
public class AuthCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthCheckManager authCheckManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            AuthCheck authCheck = ((HandlerMethod) handler).getMethod().getAnnotation(AuthCheck.class);
            if (authCheck == null) {
                return true;
            }
            SessionUser sessionUser = SessionContext.getUser();
            if (sessionUser.getIsSuper()) {
                /**默认超级管理员拥有所有权限*/
                return true;
            }

            AuthCheckTypeEnum checkType = authCheck.checkType();
            String paramName = authCheck.paramName();
            Long id = Long.parseLong(request.getParameter(paramName));

            if (!authCheckManager.checkAuth(sessionUser.getId(), sessionUser.getGroupIds(), id, checkType, authCheck.operateType())) {
                ResponseUtils.noAuth(request, response);
                return false;
            }

        }

        return true;
    }

}
