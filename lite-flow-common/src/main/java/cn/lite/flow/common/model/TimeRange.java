package cn.lite.flow.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @description: 时间区间
 * @author: yueyunyue
 * @create: 2018-07-15 08:52
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class TimeRange {

    private Date startTime;

    private Date endTime;

}
