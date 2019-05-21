package cn.lite.flow.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 参数相关
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class ParamExpressionUtils {


    private final static String TIME_VARIABLE_REGEX = "\\$\\{time:(.*?)\\}";

    private final static Pattern TIME_VARIABLE_PATTERN = Pattern.compile(TIME_VARIABLE_REGEX);

    /**
     *
     * @param param
     * @param taskVersion
     * @return
     */
    public static String handleTimeExpression(String param, String taskVersion){

        if(StringUtils.isBlank(param)){
            return null;
        }

        Matcher matcher = TIME_VARIABLE_PATTERN.matcher(param);

        while (matcher.find()){
            String expression = matcher.group(0);
            String timeExpression = matcher.group(1);
            String calculateTime = TimeExpressionUtils.calculateTimeExpression(timeExpression, taskVersion);
            param = StringUtils.replace(param, expression, calculateTime);

        }
        return param;
    }

    /**
     * 将表达式转换为date类型，主要用在上下游依赖添加参数后，计算时间区间使用
     * @param expression
     * @param taskVersion
     * @return
     */
    public static Date expression2Date(String expression, String taskVersion){

        String dateStr = handleTimeExpression(expression, taskVersion);
        if(StringUtils.isNotBlank(dateStr)){
            StringBuilder dateNumSb = new StringBuilder();
            for(int i = 0; i < dateStr.length(); i ++){
                char charItem = dateStr.charAt(i);
                //只要数字
                if(Character.isDigit(charItem)){
                    dateNumSb.append(Character.getNumericValue(charItem));
                }
            }
            String dateNumStr = dateNumSb.toString();
            Date dateParam = TimeExpressionUtils.getDateByVersion(dateNumStr);
            return dateParam;
        }

        return null;
    }

    /**
     * 将文本内容更改一下
     * @param content
     * @param paramMap
     * @return
     */
    public static String handleParam(String content, Map<String, ?> paramMap){
        String template = content;
        Object taskVersionNo = paramMap.get(CommonConstants.PARAM_CONSOLE_TASK_VERSION);
        if(taskVersionNo != null){
            template = handleTimeExpression(content, String.valueOf(taskVersionNo));
        }
        return FreeMarkerUtils.formatString(template, paramMap);
    }


}
