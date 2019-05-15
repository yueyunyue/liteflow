package cn.lite.flow.common.time;

import cn.lite.flow.common.model.consts.TimeUnit;
import cn.lite.flow.common.time.calculator.DayTimeCalculator;
import cn.lite.flow.common.time.calculator.HourTimeCalculator;
import cn.lite.flow.common.time.calculator.MinuteTimeCalculator;
import cn.lite.flow.common.time.calculator.MonthTimeCalculator;
import cn.lite.flow.common.time.calculator.TimeCalculator;
import cn.lite.flow.common.time.calculator.WeekTimeCalculator;
import cn.lite.flow.common.time.calculator.YearTimeCalculator;

import java.util.Date;

/**
 * @description: 时间计算器的工厂
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class TimeCalculatorFactory {

    /**
     * 获取时间计算器
     * @param timeUnit
     * @return
     */
    public static TimeCalculator getCalculator(TimeUnit timeUnit){

        switch (timeUnit){
            case MINUTE:
                return new MinuteTimeCalculator();
            case HOUR:
                return new HourTimeCalculator();
            case DAY:
                return new DayTimeCalculator();
            case WEEK:
                return new WeekTimeCalculator();
            case MONTH:
                return new MonthTimeCalculator();
            case YEAR:
                return new YearTimeCalculator();
        }

        return null;
    }

    /**
     * 获取时间计算器
     * @param timeUnit
     * @param time
     * @return
     */
    public static TimeCalculator getCalculator(TimeUnit timeUnit, Date time) {

        switch (timeUnit){
            case MINUTE:
                return new MinuteTimeCalculator(time);
            case HOUR:
                return new HourTimeCalculator(time);
            case DAY:
                return new DayTimeCalculator(time);
            case WEEK:
                return new WeekTimeCalculator(time);
            case MONTH:
                return new MonthTimeCalculator(time);
            case YEAR:
                return new YearTimeCalculator(time);
        }

        return null;
    }

}
