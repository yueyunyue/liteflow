package cn.lite.flow.executor.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * Job查询模型
 */
@Data
@ToString(callSuper = true)
public class ExecutorContainerQM extends BaseQM {

    private String nameLike;        //按照名称模糊查询

}
