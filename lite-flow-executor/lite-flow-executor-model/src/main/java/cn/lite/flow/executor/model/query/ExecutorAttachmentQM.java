package cn.lite.flow.executor.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ExecutorAttachmentQM extends BaseQM {

    private Long id;                    //id

    private String nameLike;            //按名称模糊查询

    private String name;                //名称

    private Integer type;               //类型

    private Integer status;             //状态

}
