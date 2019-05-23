package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 任务流查询模型
 */
@Data
@ToString(callSuper = true)
public class TaskQM extends BaseQM {

    private Long pluginId;                    //插件id

    private String name;                      //name精确查询

    private String nameLike;                  //name模糊查询

    private Integer period;                   //周期

    private Long userId;                      //用户id
}
