package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 任务流查询模型
 */
@Data
@ToString(callSuper = true)
public class TaskVersionQM extends BaseQM {

    private Long taskId;                          //任务id

    private Long taskVersionNo;                   //任务版本号

    private Integer finalStatus;                  //最终状态

    public static final String COL_VERSION_NO = "version_no";    //列版本号



}
