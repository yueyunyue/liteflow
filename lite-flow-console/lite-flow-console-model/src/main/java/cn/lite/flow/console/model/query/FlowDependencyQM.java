package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 任务流与依赖之间的查询模型
 */
@Data
@ToString(callSuper = true)
public class FlowDependencyQM extends BaseQM {

    private Long flowId;                 //任务流id

    private Long taskDependencyId;       //任务依赖id

    private List<Long> taskDependencyIds;//任务依赖ids

}
