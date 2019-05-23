package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 任务流查询模型
 */
@Data
@ToString(callSuper = true)
public class FlowQM extends BaseQM {

    private String name;            //名称精确查询

    private String nameLike;        //名称模糊查询

    private Integer status;         //状态

    public static final String COL_UPDATE_TIME = "update_time";

}
