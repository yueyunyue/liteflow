package cn.lite.flow.common.time.calculator;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * @description: 时间粒度为日的，时间计算
 * @author: yueyunyue
 * @create: 2018-07-15 08:58
 **/
public class DayTimeCalculator extends TimeCalculator {

    public DayTimeCalculator() {
    }

    public DayTimeCalculator(Date time) {
        this.time = time;
    }

    @Override
    public Date getStartTime() {
        DateTime dateTime = new DateTime(time);
        return dateTime.withTimeAtStartOfDay().toDate();
    }

    @Override
    public Date getEndTime() {
        DateTime dateTime = new DateTime(time);
        return dateTime.millisOfDay().withMaximumValue().toDate();
    }
}
