package cn.lite.flow.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ly on 2018/10/31.
 */
public class CookieUtils {

    private static AESUtils aesUtils = AESUtils.getInstance();

    /**
     * 将userId写入cookie
     *
     * @param userId        用户id
     * @param response      响应
     * @param expiry        过期时间
     */
    public static void writeUserCookie(Long userId, HttpServletResponse response, int expiry) {
        String siteKey = aesUtils.encrypt(RandomUtils.letterOrDigital(8) + userId + RandomUtils.letterOrDigital(4),
                Constants.LITE_FLOW_COOKIE_SALT);
        StringBuilder cookieInfo = new StringBuilder("userId=");
        cookieInfo.append(userId)
                .append("&siteKey=")
                .append(siteKey);

        Cookie cookie = new Cookie(Constants.LITE_FLOW_COOKIE_NAME, cookieInfo.toString());
        cookie.setMaxAge(expiry);
//        cookie.setDomain(Constants.LITE_FLOW_COOKIE_DOMIAN);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 删除cookie
     *
     * @param response      响应
     */
    public static void removeUserCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(Constants.LITE_FLOW_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 从请求中获取用户信息
     *
     * @param request   请求
     * @return
     */
    public static Long getUserId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length < 1) {
            return null;
        }

        String cookieInfo = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.LITE_FLOW_COOKIE_NAME)) {
                cookieInfo = cookie.getValue();
                break;
            }
        }
        if (StringUtils.isBlank(cookieInfo)) {
            return null;
        }
        Map<String, String> cookieData = parseData(cookieInfo, "&");
        String userId = cookieData.get("userId");
        String siteKey = cookieData.get("siteKey");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(siteKey)) {
            return null;
        }
        String decryptSiteKey = aesUtils.decrypt(siteKey, Constants.LITE_FLOW_COOKIE_SALT);
        String encryUserId = decryptSiteKey.substring(8, decryptSiteKey.length() - 4);
        if (userId.equals(encryUserId)) {
            return Long.parseLong(userId);
        }
        return null;
    }

    private static Map<String, String> parseData(String data, String split) {
        Map<String, String> result = Collections.EMPTY_MAP;
        if (StringUtils.isNotBlank(data) && StringUtils.isNotBlank(split)) {
            result = new HashMap<>();
            String[] dataSplitArr = data.split(split);
            for (String dataSplit : dataSplitArr) {
                String[] keyValue = dataSplit.split("=");
                if (keyValue != null && keyValue.length == 2) {
                    result.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return result;
    }
}
