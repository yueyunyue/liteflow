package cn.lite.flow.console.web.handler;

import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ly on 2018/12/14.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String handle(Exception ex) {

        if (ex instanceof IllegalArgumentException || ex instanceof ConsoleRuntimeException) {
            LOG.error("request error", ex);
            return ResponseUtils.error(ex.getMessage());
        }
        LOG.error("Console exception", ex);
        return ResponseUtils.error("服务器内部错误");
    }
}
