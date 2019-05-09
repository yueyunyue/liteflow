package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 任务流查询模型
 */
@Data
@ToString(callSuper = true)
public class TaskDependencyQM extends BaseQM {

    private Long taskId;                 //任务依赖id

    private Long upstreamTaskId;         //上游任务id


}
