package cn.lite.flow.executor.client;
import cn.lite.flow.executor.client.model.ContainerParam;
import cn.lite.flow.executor.model.basic.ExecutorContainer;

import java.util.List;

/**
 * Created by ly on 2018/11/14.
 */
public interface ExecutorContainerRpcService {

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ExecutorContainer getById(long id);

    /**
     * 添加
     *
     * @param container
     * @return      返回id
     */
    Long add(ExecutorContainer container);

    /**
     * 更新
     *
     * @param container
     * @return
     */
    int update(ExecutorContainer container);

    /**
     * 获取容器
     * @param containerParam
     * @return
     */
    List<ExecutorContainer> list(ContainerParam containerParam);

    /**
     * 获取数量
     * @param containerParam
     * @return
     */
    int count(ContainerParam containerParam);

    /**
     * 获取所有有效的容器
     *
     * @return
     */
    List<ExecutorContainer> listAllValid();

}
