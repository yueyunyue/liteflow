package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.query.TaskVersionQM;
import org.apache.ibatis.annotations.Param;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskVersionMapper extends BaseMapper<TaskVersion, TaskVersionQM> {

    int updateWithStatus(@Param("taskVersion") TaskVersion taskVersion, @Param("originStatus") int status);

    TaskVersion getTaskVersion(@Param("taskId")long taskId, @Param("taskVersionNo")long taskVersionNo);

    /**
     * 获取小于某个任务版本的数量
     * @param taskId
     * @param finalStatus
     * @param versionNo
     * @return
     */
    int getLessThanNoCount(@Param("taskId")long taskId, @Param("finalStatus") int finalStatus, @Param("versionNo")long versionNo);
}
