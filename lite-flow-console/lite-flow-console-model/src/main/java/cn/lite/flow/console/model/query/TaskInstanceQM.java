package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 任务流查询模型
 */
@Data
@ToString
public class TaskInstanceQM extends BaseQM {

    private List<Long> ids;                   //id列表

    private Long taskId;                      //任务id

    private Long versionId;                   //任务版本id

    private List<Long> inVersionIds;          //任务版本id列表

    private Long versionNo;                   //任务版本号

    private Date logicRunTimeLessEqual;       //逻辑开始时间小于等于

    private Long greaterThanId;               //任务版本号

    public final static String COL_LOGIC_RUN_TIME = "logic_run_time";


}
