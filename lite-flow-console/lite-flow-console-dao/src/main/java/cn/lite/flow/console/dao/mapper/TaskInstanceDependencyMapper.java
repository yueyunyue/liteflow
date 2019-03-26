package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.TaskInstanceDependency;
import cn.lite.flow.console.model.query.TaskInstanceDependencyQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskInstanceDependencyMapper extends BaseMapper<TaskInstanceDependency, TaskInstanceDependencyQM> {

    /**
     * 批量添加
     * @param dependencies
     */
    void insertBatch(@Param("dependencies") List<TaskInstanceDependency> dependencies);

}
