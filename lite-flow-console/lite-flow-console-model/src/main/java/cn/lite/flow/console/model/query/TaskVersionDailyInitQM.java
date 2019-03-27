package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 任务流查询模型
 */
@Data
@ToString
public class TaskVersionDailyInitQM extends BaseQM {

    private Long taskId;

    private List<Long> inTaskIds;           //taskId列表查询

    private Long day;

    public static final String COL_DAY = "day";

}
