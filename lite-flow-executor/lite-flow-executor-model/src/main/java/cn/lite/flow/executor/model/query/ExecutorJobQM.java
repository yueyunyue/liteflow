package cn.lite.flow.executor.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 任务查询模型
 */
@Data
@ToString(callSuper = true)
public class ExecutorJobQM extends BaseQM {

    private String applicationId;       //应用id

    private Long pluginId;              //插件id

    private Long executorServerId;      //执行者id

    private Long containerId;           //容器id

    private Long sourceId;              //任务来源id

}
