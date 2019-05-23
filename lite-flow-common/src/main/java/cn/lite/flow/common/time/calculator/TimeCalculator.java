package cn.lite.flow.common.time.calculator;

import cn.lite.flow.common.model.TimeRange;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @description: 时间计算
 * @author: yueyunyue
 * @create: 2018-07-15 08:50
 **/
@Setter
@Getter
public abstract class TimeCalculator {

    protected Date time;

    /**
     * 获取时间的开始
     * @return
     */
    public abstract Date getStartTime();

    /**
     * 获取时间的结束
     * @return
     */
    public abstract Date getEndTime();

    /**
     * 获取时间区间
     * @return
     */
    public final TimeRange getRange(){

        return new TimeRange(getStartTime(), getEndTime());
    }

}
