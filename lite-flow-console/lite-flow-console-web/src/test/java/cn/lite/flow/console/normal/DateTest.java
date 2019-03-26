package cn.lite.flow.console.normal;

import cn.lite.flow.common.utils.DateUtils;
import org.junit.Test;

import java.util.Date;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-03-20
 **/
public class DateTest {

    @Test
    public void testBetween(){
        Date now = DateUtils.getNow();
        Date date = DateUtils.addMinute(now, -100);
        long betweenMinutes = DateUtils.betweenMinutes(date, now);
        System.out.println(betweenMinutes);
    }
    @Test
    public void testEndDate(){
        Date now = DateUtils.getNow();
        System.out.println(DateUtils.getEndTimeOfDay(now));
    }

}
