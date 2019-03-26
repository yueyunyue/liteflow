package cn.lite.flow.executor.client;

import cn.lite.flow.executor.client.model.PluginParam;
import cn.lite.flow.executor.model.basic.ExecutorPlugin;

import java.util.List;

/**
 * Created by ly on 2018/11/14.
 */
public interface ExecutorPluginRpcService {

    /**
     * 添加
     *
     * @param executorPlugin
     * @return  返回id
     */
    long add(ExecutorPlugin executorPlugin);

    /**
     * 更新
     *
     * @param executorPlugin
     * @return
     */
    int update(ExecutorPlugin executorPlugin);

    /**
     * 获取列表
     * @return  返回10条
     */
    List<ExecutorPlugin> list(PluginParam pluginParam);

    /**
     * 获取数量
     * @param pluginParam
     * @return
     */
    int count(PluginParam pluginParam);

    /**
     * 获取插件
     * @param id
     * @return
     */
    ExecutorPlugin getById(long id);
    /**
     * 获取所有有效的容器
     *
     * @return
     */
    List<ExecutorPlugin> listAllValid();
}
