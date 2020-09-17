package cn.lite.flow.console.calucator;

import cn.lite.flow.console.common.utils.QuartzUtils;
import org.junit.Test;

/**
 * @description:
 * @author: cyp
 * @create: 2020-09-16
 **/
public class TimeTest {

    public static void period(String cron){
        System.out.println("cron:" + cron + " is period:  " + QuartzUtils.getPeriodByCron(cron));
    }
    @Test
    public void testPeriod(){
        System.out.println("--------------MINUTE-------------");
        period("0/5 * * * ?");
        period("30,45 * * * ?");
        System.out.println("--------------HOUR-------------");
        period("0 0/5 * * ?");
        period("0 1,3,5 * * ?");
        System.out.println("--------------DAY-------------");
        period("0 5 * * ?");
        period("0 1 * * ?");
        period("0 0 */3 * ?");
        period("15 10 ? * MON-FRI");
        System.out.println("--------------WEEK-------------");
        period("5 10 ? * 5L");
        System.out.println("--------------MONTH-------------");
        period("15 10 ? * 5L");
//        period("0 5 * * ?");
    }

    @Test
    public void testOnePeriod(){
        period("15 10 ? * 5L");
    }
}
