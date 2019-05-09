package cn.lite.flow.executor.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 任务查询模型
 */
@Data
@ToString(callSuper = true)
public class ExecutorCallbackQM extends BaseQM {

    private Integer callbackStatus;     //回调状态

    private Long executorServerId;      //执行者id

    private Long jobId;                 //任务id

}
