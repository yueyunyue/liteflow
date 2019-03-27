package cn.lite.flow.executor.dao;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.executor.model.basic.ExecutorCallback;
import cn.lite.flow.executor.model.query.ExecutorCallbackQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 回调dao
 */
public interface ExecutorCallbackMapper extends BaseMapper<ExecutorCallback, ExecutorCallbackQM> {

    /**
     *
     * @param ids
     * @return
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") int status);

}