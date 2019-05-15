package cn.lite.flow.common.time.calculator;

import cn.lite.flow.common.model.consts.CommonConstants;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @description: 时间粒度为时的，时间计算
 * @author: yueyunyue
 * @create: 2018-07-15 08:58
 **/
public class HourTimeCalculator extends TimeCalculator {

    public HourTimeCalculator() {
    }

    public HourTimeCalculator(Date time) {
        this.time = time;
    }

    @Override
    public Date getStartTime() {
        DateTime dateTime = new DateTime(time);
        dateTime = dateTime.withMinuteOfHour(CommonConstants.ZERO);
        dateTime = dateTime.withSecondOfMinute(CommonConstants.ZERO);
        return dateTime.toDate();
    }

    @Override
    public Date getEndTime() {
        DateTime dateTime = new DateTime(time);
        dateTime = dateTime.withMinuteOfHour(CommonConstants.FIFTY_NINE);
        dateTime = dateTime.withSecondOfMinute(CommonConstants.FIFTY_NINE);
        return dateTime.toDate();
    }
}
