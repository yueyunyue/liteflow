package cn.lite.flow.common.time.calculator;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * @description: 时间粒度为月的，时间计算
 * @author: yueyunyue
 * @create: 2018-07-15 09:13
 **/
public class MonthTimeCalculator extends TimeCalculator {

    public MonthTimeCalculator() {
    }

    public MonthTimeCalculator(Date time) {
        this.time = time;
    }

    @Override
    public Date getStartTime() {
        DateTime dateTime = new DateTime(time);
        DateTime startTime = dateTime.dayOfMonth().withMinimumValue();
        return startTime.withTimeAtStartOfDay().toDate();
    }

    @Override
    public Date getEndTime() {
        DateTime dateTime = new DateTime(time);
        DateTime endTime = dateTime.dayOfMonth().withMaximumValue();
        return endTime.millisOfDay().withMaximumValue().toDate();
    }
}
