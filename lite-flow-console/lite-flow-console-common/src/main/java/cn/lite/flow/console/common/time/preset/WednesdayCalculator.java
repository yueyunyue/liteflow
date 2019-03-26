package cn.lite.flow.console.common.time.preset;

import cn.lite.flow.common.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * Created by ly on 2018/9/19.
 */
public class WednesdayCalculator implements TimeParamCalculator {

    @Override
    public DateTime calculate(DateTime dateTime) {
        return dateTime.withDayOfWeek(3);
    }

    @Override
    public String format(DateTime dateTime) {
        return dateTime.toString(DateUtils.COMMON_DATE_FORMAT);
    }
}
