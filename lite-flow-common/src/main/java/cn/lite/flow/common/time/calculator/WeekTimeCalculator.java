package cn.lite.flow.common.time.calculator;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * @description: 时间粒度为周的，时间计算
 * @author: yueyunyue
 * @create: 2018-07-15 08:58
 **/
public class WeekTimeCalculator extends TimeCalculator {

    public WeekTimeCalculator() {
    }

    public WeekTimeCalculator(Date time) {
        this.time = time;
    }

    @Override
    public Date getStartTime() {
        DateTime dateTime = new DateTime(time);
        DateTime startTime = dateTime.dayOfWeek().withMinimumValue();
        return startTime.withTimeAtStartOfDay().toDate();
    }

    @Override
    public Date getEndTime() {
        DateTime dateTime = new DateTime(time);
        DateTime endTime = dateTime.dayOfWeek().withMaximumValue();
        return endTime.millisOfDay().withMaximumValue().toDate();
    }
}
