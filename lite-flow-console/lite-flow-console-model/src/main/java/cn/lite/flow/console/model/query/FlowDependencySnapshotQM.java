package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 任务流快照查询模型
 */
@Data
@ToString(callSuper = true)
public class FlowDependencySnapshotQM extends BaseQM {

    private Long flowId;                 //任务流id

    private Long taskId;                 //任务依赖id

    private Long upstreamTaskId;         //上游任务id

}
