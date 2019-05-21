package cn.lite.flow.common.time.calculator;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * @description: 时间粒度为年的，时间计算
 * @author: yueyunyue
 * @create: 2018-07-15 08:58
 **/
public class YearTimeCalculator extends TimeCalculator {

    public YearTimeCalculator() {
    }

    public YearTimeCalculator(Date time) {
        this.time = time;
    }

    @Override
    public Date getStartTime() {
        DateTime dateTime = new DateTime(time);
        DateTime startTime = dateTime.dayOfYear().withMinimumValue();
        return startTime.withTimeAtStartOfDay().toDate();
    }

    @Override
    public Date getEndTime() {
        DateTime dateTime = new DateTime(time);
        DateTime endTime = dateTime.dayOfYear().withMaximumValue();
        return endTime.millisOfDay().withMaximumValue().toDate();
    }
}
