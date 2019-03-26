package cn.lite.flow.console.dao.mapper;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.console.model.basic.TaskVersionDailyInit;
import cn.lite.flow.console.model.query.TaskVersionDailyInitQM;

import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskVersionDailyInitMapper extends BaseMapper<TaskVersionDailyInit, TaskVersionDailyInitQM> {
    /**
     * 批量添加
     * @param dailyInits
     */
    void batchInsert(List<TaskVersionDailyInit> dailyInits);

}
