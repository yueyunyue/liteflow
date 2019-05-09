package cn.lite.flow.console.model.query;

import cn.lite.flow.common.model.query.BaseQM;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by ly on 2018/10/18.
 */
@Data
@ToString(callSuper = true)
public class UserGroupAuthMidQM extends BaseQM {

    private Long sourceId;          //源id

    private Integer sourceType;     //源类型

    private List<Long> sourceIds;   //源id列表

    private Long targetId;          //目的id

    private Integer targetType;     //目的类型

}
