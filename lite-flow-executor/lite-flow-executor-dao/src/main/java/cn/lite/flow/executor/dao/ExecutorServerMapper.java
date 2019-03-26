package cn.lite.flow.executor.dao;

import cn.lite.flow.common.dao.basic.BaseMapper;
import cn.lite.flow.executor.model.basic.ExecutorServer;
import cn.lite.flow.executor.model.query.ExecutorServerQM;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by ly on 2018/12/14.
 */
public interface ExecutorServerMapper extends BaseMapper<ExecutorServer, ExecutorServerQM> {

    /**
     * 根据ip
     * @param ip
     * @return
     */
    ExecutorServer getByIp(String ip);

}
