package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * 任务流查询模型
 */
@Data
@ToString(callSuper = true)
public class ModelPropertyQM extends BaseQM {

    private Long modelId;              //实体id

    private Integer type;              //类型

    private String name;               //名称

}
