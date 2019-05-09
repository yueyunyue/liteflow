package cn.lite.flow.executor.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

/**
 * Created by ly on 2018/12/14.
 */
@Data
@ToString(callSuper = true)
public class ExecutorServerQM extends BaseQM {

    private String nameLike;        //按照名称模糊查询

    private String ip;
}
