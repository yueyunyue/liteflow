package cn.lite.flow.executor.client;

import cn.lite.flow.executor.client.model.ServerParam;
import cn.lite.flow.executor.model.basic.ExecutorServer;
import java.util.List;

/**
 * Created by ly on 2018/12/14.
 */
public interface ExecutorServerRpcService {

    /**
     * 根据id获取执行者
     * @param id
     * @return
     */
    ExecutorServer getById(long id);
    /**
     * 添加
     * @param executor      执行者
     * @return              id
     */
    Long add(ExecutorServer executor);

    /**
     * 更新
     * @param executor      要更新的执行者
     * @return              更新的数量
     */
    int update(ExecutorServer executor);

    /**
     * 获取列表
     * @return  返回10条
     */
    List<ExecutorServer> list(ServerParam serverParam);

    /**
     * 获取数量
     * @param serverParam
     * @return
     */
    int count(ServerParam serverParam);

}
