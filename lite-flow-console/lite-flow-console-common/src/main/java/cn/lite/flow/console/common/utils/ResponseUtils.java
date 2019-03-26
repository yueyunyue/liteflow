package cn.lite.flow.console.common.utils;

import cn.lite.flow.common.utils.Constants;
import cn.lite.flow.common.utils.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * Created by ly on 2018/9/13.
 */
public class ResponseUtils {

    public static final int STATUS_SUCCESS = 0;               //成功

    public static final int STATUS_ERROR = -1;                //失败

    public static final int STATUS_NO_AUTH = -2;              //没有权限错误码

    public static final String MSG_NO_AUTH = "NO_AUTH";       //没有权限说明

    public static final int STATUS_NOT_LOGIN = -3;            //没有登录错误码

    public static final String MSG_NO_LOGIN = "NOT_LOGIN";     //没有登录说明


    /**
     * 返回记录总数和数据列表
     *
     * @param total     记录总数
     * @param data      数据
     * @return
     */
    public static String list(int total, Object data) {
        JSONObject result = new JSONObject();
        result.put("status", STATUS_SUCCESS);
        result.put("total", total);
        result.put("data", data);
        return result.toJSONString();
    }

    /**
     * 空列表
     * @return
     */
    public static String listEmpty() {
        JSONObject result = new JSONObject();
        result.put("status", STATUS_SUCCESS);
        result.put("total", 0);
        result.put("data", Collections.emptyList());
        return result.toJSONString();
    }

    /**
     * 返回数据
     *
     * @param data      数据
     * @return
     */
    public static String success(Object data) {
        JSONObject result = new JSONObject();
        result.put("status", STATUS_SUCCESS);
        result.put("data", data);
        return JSONUtils.toJSONStringWithoutCircleDetect(result);
    }

    /**
     * 返回成功消息
     *
     * @param msg   提示信息
     * @return
     */
    public static String success(String msg) {
        JSONObject result = new JSONObject();
        result.put("status", STATUS_SUCCESS);
        result.put("data", msg);
        return JSONUtils.toJSONStringWithoutCircleDetect(result);
    }

    /**
     * 返回错误消息
     *
     * @param msg   提示信息
     * @return
     */
    public static String error(String msg) {
        JSONObject result = new JSONObject();
        result.put("status", STATUS_ERROR);
        result.put("data", msg);
        return JSONUtils.toJSONStringWithoutCircleDetect(result);
    }

    /**
     * 携带错误码和错误信息
     *
     * @param code
     * @param msg
     * @return
     */
    public static String error(int code, String msg) {
        JSONObject result = new JSONObject();
        result.put("status", code);
        result.put("data", msg);
        return JSONUtils.toJSONStringWithoutCircleDetect(result);
    }

    /**
     * 没有权限写入信息
     *
     * @param request
     * @param response
     */
    public static void noAuth(HttpServletRequest request, HttpServletResponse response) {
        errorMsg(request, response, STATUS_NO_AUTH, MSG_NO_AUTH);
    }

    /**
     * 未登录写入信息
     *
     * @param request
     * @param response
     */
    public static void notLogin(HttpServletRequest request, HttpServletResponse response) {
        errorMsg(request, response, STATUS_NOT_LOGIN, MSG_NO_LOGIN);
    }
    public static String notLogin() {
        JSONObject result = new JSONObject();
        result.put("status", STATUS_NOT_LOGIN);
        result.put("data", MSG_NO_LOGIN);
        return JSONUtils.toJSONStringWithoutCircleDetect(result);
    }

    public static void errorMsg(HttpServletRequest request, HttpServletResponse response, int code, String msg) {
        String requestType = request.getHeader("X-Requested-With");

        if (StringUtils.equalsIgnoreCase(requestType, "XMLHttpRequest")) {
            response.reset();
            response.setContentType("application/X-JSON;charset=UTF-8");
            PrintWriter printWriter = null;
            try {
                printWriter = response.getWriter();
                printWriter.write(error(code, msg));
                printWriter.flush();
            } catch (IOException e) {

            } finally {
                IOUtils.closeQuietly(printWriter);
            }
        } else {
            try {
                response.sendRedirect(request.getContextPath() + Constants.LITE_FLOW_LOGIN_URL);
            } catch (IOException e) {

            }
        }
    }

}
