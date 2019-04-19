package cn.lite.flow.console.kernel.event.handler.check;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.console.model.event.model.ScheduleEvent;

/**
 * @description: 验证是否符合条件
 * @author: yueyunyue
 * @create: 2018-08-06
 **/
public interface Checker<T> {
    /**
     * 验证
     * @param t
     * @return
     */
    Tuple<Boolean, String> check(ScheduleEvent event, T t);

}
