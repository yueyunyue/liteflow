package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 任务实例查询模型
 */
@Data
@ToString(callSuper = true)
public class TaskInstanceDependencyQM extends BaseQM {

    private Long taskId;                        //任务id

    private Long instanceId;                    //实例id

    private Long upstreamTaskId;                //上游任务id

    private Long upstreamTaskVersionNo;         //上游任务版本


}
