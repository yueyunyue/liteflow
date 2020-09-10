package cn.lite.flow.console.calucator;

import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.common.model.consts.TimeUnit;
import cn.lite.flow.common.model.TimeRange;
import cn.lite.flow.common.time.TimeCalculatorFactory;
import cn.lite.flow.common.time.preset.TimeParamCalculator;
import cn.lite.flow.common.utils.ParamExpressionUtils;
import cn.lite.flow.common.utils.TimeExpressionUtils;
import cn.lite.flow.console.common.utils.*;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: test
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class CalucatorTest {

    @Test
    public void test(){

        Date now = new Date();

        System.out.println(TimeCalculatorFactory.getCalculator(TimeUnit.MINUTE, now).getRange());
        System.out.println(TimeCalculatorFactory.getCalculator(TimeUnit.HOUR, now).getRange());
        System.out.println(TimeCalculatorFactory.getCalculator(TimeUnit.DAY, now).getRange());
        System.out.println(TimeCalculatorFactory.getCalculator(TimeUnit.WEEK, now).getRange());
        System.out.println(TimeCalculatorFactory.getCalculator(TimeUnit.YEAR, now).getRange());

        System.out.println(TaskVersionUtils.getTaskVersion(now, TimeUnit.MINUTE));
        System.out.println(TaskVersionUtils.getTaskVersion(now, TimeUnit.HOUR));
        System.out.println(TaskVersionUtils.getTaskVersion(now, TimeUnit.DAY));
        System.out.println(TaskVersionUtils.getTaskVersion(now, TimeUnit.WEEK));
        System.out.println(TaskVersionUtils.getTaskVersion(now, TimeUnit.MONTH));
        System.out.println(TaskVersionUtils.getTaskVersion(now, TimeUnit.YEAR));

        for (Map.Entry<String, TimeParamCalculator> entry : TimeExpressionUtils.PRESET_PARMAS.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().format(entry.getValue().calculate(new DateTime(now))));
        }

    }

    @Test
    public void testCron(){

        Date now = new Date();

        TimeRange range = TimeCalculatorFactory.getCalculator(TimeUnit.DAY, now).getRange();

        List<Date> runtimes = QuartzUtils.getRunDateTimes("0 0/30 * * * ?", range.getStartTime(), range.getEndTime());

        runtimes.forEach(date -> {
            System.out.println(DateUtils.formatToDateTimeStr(date));
        });

    }

    @Test
    public void timeExpression(){

        String expression = "startDate=${time: yyyy-MM-dd0000, -1d},\n" +
                "today=${time: today},\n" +
                "todayFormat=${time: yyyyMMdd,today},\n" +
                "yesterday=${time: today, -1d},\n" +
                "theDayBeforeYesterday=${time: yyyyMMdd,today, 0d}";
        String result = ParamExpressionUtils.handleTimeExpression(expression, "20190101");
        System.out.println(result);

    }

    @Test
    public void testExpressioinDate(){

        String taskVersion = "20190101";
        Date date = ParamExpressionUtils.expression2Date("${time: today, -2d}", taskVersion);
        System.out.println(date);

    }
    @Test
    public void testExpressioins(){
        String taskVersion = "20190131";
        System.out.println("1.传参为一个参数");
        String mode1 = "${time: yesterday}";
        String mode1Result = ParamExpressionUtils.handleTimeExpression(mode1, taskVersion);
        System.out.println(mode1 + ":" + mode1Result);

        System.out.println("2.传参为两个参数");
        String mode2 = "${time: yesterday, -1d}";
        String mode2Result = ParamExpressionUtils.handleTimeExpression(mode2, taskVersion);
        System.out.println(mode2 + ":" + mode2Result);

        String mode22 = "${time: yyyyMMddhhmmss, -1d}";
        String mode22Result = ParamExpressionUtils.handleTimeExpression(mode22, taskVersion);
        System.out.println(mode22 + ":" + mode22Result);

        String mode23 = "${time: yyyyMMddhhmmss, yesterday}";
        String mode23Result = ParamExpressionUtils.handleTimeExpression(mode23, taskVersion);
        System.out.println(mode23 + ":" + mode23Result);

        System.out.println("3.传参为三个参数");
        String mode3 = "${time: yyyyMMddhhmmss, yesterday, -1d}";
        String mode3Result = ParamExpressionUtils.handleTimeExpression(mode3, taskVersion);
        System.out.println(mode3 + ":" + mode3Result);


    }

    @Test
    public void testNextFireTime(){
        Date date = new Date();
        Date nextFireTimeWithDefaultTimeZone = QuartzUtils.getNextFireTimeWithDefaultTimeZone(date, "30/0 * * * ?");
        System.out.println(DateUtils.formatToDateTimeStr(nextFireTimeWithDefaultTimeZone));
    }

}
