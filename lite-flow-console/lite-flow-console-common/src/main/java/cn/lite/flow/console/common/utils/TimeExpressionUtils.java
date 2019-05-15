package cn.lite.flow.console.common.utils;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.common.model.consts.TimeUnit;
import cn.lite.flow.common.time.preset.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;


/**
 * 时间表达式相关
 */
public class TimeExpressionUtils {

    public static final Map<String, TimeParamCalculator> PRESET_PARMAS;

    static {

        PRESET_PARMAS = Maps.newHashMap();
        PRESET_PARMAS.put("today", new TodayCalculator());
        PRESET_PARMAS.put("yesterday", new YesterdayCalculator());
        PRESET_PARMAS.put("tomorrow", new TomorrowCalculator());
        /**
         * 周数据
         */
        PRESET_PARMAS.put("Monday", new MondayCalculator());
        PRESET_PARMAS.put("Tuesday", new TuesdayCalculator());
        PRESET_PARMAS.put("Wednesday", new WednesdayCalculator());
        PRESET_PARMAS.put("Thursday", new ThursdayCalculator());
        PRESET_PARMAS.put("Friday", new FridayCalculator());
        PRESET_PARMAS.put("Saturday", new SaturdayCalculator());
        PRESET_PARMAS.put("Sunday", new SundayCalculator());

        //上周
        PRESET_PARMAS.put("lastMonday", new LastMondayCalculator());
        PRESET_PARMAS.put("lastTuesday", new LastTuesdayCalculator());
        PRESET_PARMAS.put("lastWednesday", new LastWednesdayCalculator());
        PRESET_PARMAS.put("lastThursday", new LastThursdayCalculator());
        PRESET_PARMAS.put("lastFriday", new LastFridayCalculator());
        PRESET_PARMAS.put("lastSaturday", new LastSaturdayCalculator());
        PRESET_PARMAS.put("lastSunday", new LastSundayCalculator());

        /**
         * 月
         */
        PRESET_PARMAS.put("monthFirstDay", new MonthFirstDayCalculator());
        PRESET_PARMAS.put("monthLastDay", new MonthLastDayCalculator());

        //上个月
        PRESET_PARMAS.put("lastMonthFirstDay", new LastMonthFirstDayCalculator());
        PRESET_PARMAS.put("lastMonthLastDay", new LastMonthLastDayCalculator());

    }

    /**
     * 将时间操作表达式转化为单位加数字，即1d-><1,TimeUnit.DAY>
     * @param expression
     * @return
     */
    public static Tuple<Integer, TimeUnit> parseToNumAndTimeUnit(String expression) {

        TimeUnit[] timeUnits = TimeUnit.values();
        for(TimeUnit timeUnit : timeUnits) {
            if(StringUtils.contains(expression, timeUnit.getSuffix())){
                int num = Integer.parseInt(StringUtils.replace(expression, timeUnit.getSuffix(), ""));
                return new Tuple<Integer, TimeUnit>(num, timeUnit);
            }
        }

        return null;
    }

    /**
     * 按某个时间单位添加时间
     * @param dateTime
     * @param n
     * @param timeUnit
     * @return
     */
    public static DateTime calculateTime(DateTime dateTime, int n, TimeUnit timeUnit) {

        DateTime addedDateTime = null;
        switch (timeUnit){
//            case SECOND:
//                addedDateTime = dateTime.plusSeconds(n);
//                break;
            case MINUTE:
                addedDateTime = dateTime.plusMinutes(n);
                break;
            case HOUR:
                addedDateTime = dateTime.plusHours(n);
                break;
            case DAY:
                addedDateTime = dateTime.plusDays(n);
                break;
            case WEEK:
                addedDateTime = dateTime.plusWeeks(n);
                break;
            case MONTH:
                addedDateTime = dateTime.plusMonths(n);
                break;
            case YEAR:
                addedDateTime = dateTime.plusYears(n);
                break;
        }

        return addedDateTime;
    }

    /**
     * 时间按格式生成字符串
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String formatDate(DateTime dateTime, String pattern){
        return dateTime.toString(pattern);
    }

    /**
     * 通过表达式来计算时间相关参数,即将表达式
     *
     * @param expression
     * @param taskVersion
     * @return
     */
    public static String calculateTimeExpression(String expression, String taskVersion){

        String[] params = StringUtils.split(expression, Constants.PARAM_VARIABLE_SPLIT);

        if(params == null || params.length > 3 || params.length == 0) {
            throw new IllegalArgumentException("expression：" + expression + " error");
        }

        Date date = TaskVersionUtils.getDateByVersion(taskVersion);
        DateTime versionDateTime = new DateTime(date);

        boolean isPreset = false;//变量是不是预设置

        /**
         * 等于3，说明会基于预设值来进行计算
         */
        if(params.length == 3){
            String dateFormat = StringUtils.trim(params[0]);
            String dateParam = StringUtils.trim(params[1]);
            String dateOperator = StringUtils.trim(params[2]);
            TimeParamCalculator calculator = PRESET_PARMAS.get(dateParam);
            if(calculator == null){
                throw new IllegalArgumentException("expression error:" + expression);
            }
            DateTime calculatedDateTime = calculator.calculate(versionDateTime);
            Tuple<Integer, TimeUnit> timeUnitTuple = parseToNumAndTimeUnit(dateOperator);
            DateTime dateTime = calculateTime(calculatedDateTime, timeUnitTuple.getA(), timeUnitTuple.getB());
            return formatDate(dateTime, dateFormat);
        }
        /**
         * 判断是否是预设值的值
         */
        String dateParam = StringUtils.trim(params[0]);
        TimeParamCalculator calculator = PRESET_PARMAS.get(dateParam);
        if(calculator != null){
            isPreset = true;
        }
        /**
         * 参数为2，表示有日期计算
         */
        if(params.length == 2){
            /**
             * 预设值，即预先设置好的一些参数
             */
            if(isPreset){

                DateTime calculatedDateTime = calculator.calculate(versionDateTime);
                String dateOperator = StringUtils.trim(params[1]);
                Tuple<Integer, TimeUnit> timeUnitTuple = parseToNumAndTimeUnit(dateOperator);
                DateTime dateTime = calculateTime(calculatedDateTime, timeUnitTuple.getA(), timeUnitTuple.getB());
                return calculator.format(dateTime);


            }else{
                /**
                 * 有可能只是用预设值来格式化：today=》20180101
                 */
                String dateOperator = StringUtils.trim(params[1]);
                TimeParamCalculator presetCal = PRESET_PARMAS.get(dateOperator);
                if (presetCal != null){

                    DateTime dateTime = presetCal.calculate(versionDateTime);
                    return formatDate(dateTime, dateParam);
                }else{

                    Tuple<Integer, TimeUnit> timeUnitTuple = parseToNumAndTimeUnit(dateOperator);
                    DateTime dateTime = calculateTime(versionDateTime, timeUnitTuple.getA(), timeUnitTuple.getB());
                    return formatDate(dateTime, dateParam);

                }

            }
        }

        if(params.length == 1) {
            if(isPreset){

                return calculator.format(versionDateTime);

            }else{

                return formatDate(versionDateTime, dateParam);

            }

        }
        return null;
    }


}
