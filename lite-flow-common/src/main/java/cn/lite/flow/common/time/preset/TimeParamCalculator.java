package cn.lite.flow.common.time.preset;

import org.joda.time.DateTime;

/**
 * @description: 时间参数计算器
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public interface TimeParamCalculator {
    /**
     * 获取时间并计算时使用
     * @param dateTime
     * @return
     */
    DateTime calculate(DateTime dateTime);

    /**
     * 将时间格式化成相应的格式
     * @param dateTime
     * @return
     */
    String format(DateTime dateTime);

}
