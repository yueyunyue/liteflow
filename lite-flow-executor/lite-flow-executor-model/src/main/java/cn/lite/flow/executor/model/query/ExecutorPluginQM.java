package cn.lite.flow.executor.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 插件查询模型
 */
@Data
@ToString(callSuper = true)
public class ExecutorPluginQM extends BaseQM {

    private String nameLike;            //按名称模糊查询

    private Long containerId;           //容器id

    private List<Long> ids;


}
