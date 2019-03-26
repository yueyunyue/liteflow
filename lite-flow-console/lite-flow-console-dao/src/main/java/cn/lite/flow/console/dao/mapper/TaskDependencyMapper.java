package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.TaskDependencyQM;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskDependencyMapper extends BaseMapper<TaskDependency, TaskDependencyQM> {

    void delete(long id);

}
