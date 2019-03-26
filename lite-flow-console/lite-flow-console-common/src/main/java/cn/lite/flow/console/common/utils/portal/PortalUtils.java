package cn.lite.flow.console.common.utils.portal;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ly on 2018/11/23.
 */
public class PortalUtils {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(20);

    /**
     * 提交任务
     *
     * @param callableInfos         调用信息列表
     * @param name                  任务名称
     * @param callable              任务逻辑
     * @param waitMills             获取返回结果最大等待时长
     */
    public static void submit(List<CallableInfo> callableInfos, String name, Callable<?> callable, long waitMills) {
        Future<?> future = executorService.submit(callable);
        CallableInfo callableInfo = new CallableInfo(name, future, waitMills);
        callableInfos.add(callableInfo);
    }

    /**
     * 获取所有任务的结果
     *
     * @param taskInfos
     * @return
     */
    public static Map<String, Object> getTaskResult(List<CallableInfo> taskInfos) {
        Map<String, Object> result = new HashMap<>();
        taskInfos.forEach(o -> result.put(o.getName(), o.getObject()));
        return result;
    }


}
