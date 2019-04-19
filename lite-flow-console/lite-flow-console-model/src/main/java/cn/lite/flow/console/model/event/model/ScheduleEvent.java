package cn.lite.flow.console.model.event.model;

import cn.lite.flow.console.model.event.consts.Event;
import com.google.common.base.Objects;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: 事件
 * @author: yueyunyue
 * @create: 2018-07-27
 **/
@Data
@ToString
public class ScheduleEvent implements Serializable {

    private Event eventType;               //事件

    private Long eventTargetId;            //事件目标id

    private Map<String, Object> paramMap;   //额外的数据

    /**
     * 获取一个实例
     * @param event
     * @param eventTargetId
     * @return
     */
    public static ScheduleEvent newInstance(Event event, Long eventTargetId){
        ScheduleEvent scheduleEvent = new ScheduleEvent();
        scheduleEvent.setEventType(event);
        scheduleEvent.setEventTargetId(eventTargetId);
        return scheduleEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleEvent that = (ScheduleEvent) o;
        return eventType == that.eventType &&
                Objects.equal(eventTargetId, that.eventTargetId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventType, eventTargetId);
    }
}
