package cn.lite.flow.executor.kernel.container;

import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.kernel.container.impl.NoopContainer;
import cn.lite.flow.executor.model.basic.ExecutorContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import cn.lite.flow.executor.model.kernel.Container;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import cn.lite.flow.executor.service.ExecutorContainerService;
import cn.lite.flow.executor.service.utils.ExecutorServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 获取容器
 * @author: yueyunyue
 * @create: 2019-01-09
 **/
public class ContainerFactory {

    private final static Logger LOG = LoggerFactory.getLogger(ContainerFactory.class);

    private final static ConcurrentHashMap<String, Class> CONTAINER_CLASS_MAP = new ConcurrentHashMap<>();
    /**
     * 获取容器实例
     * @param executorJob
     * @return
     */
    public static Container newInstance(ExecutorJob executorJob){

        ExecutorContainerService executorContainerService = ExecutorServiceUtils.getExecutorContainerService();
        ExecutorContainer executorContainer = executorContainerService.getById(executorJob.getContainerId());
        String className = executorContainer.getClassName();

        try{
            Class<?> cls = getClass(className);
            //参数类型
            Class<?>[] params = {ExecutorJob.class};
            //参数值
            Object[] values = {executorJob};
            //构造有两个参数的构造函数
            Constructor<?> constructor = cls.getDeclaredConstructor(params);
            //根据构造函数，传入值生成实例
            Object container = constructor.newInstance(values);
            return (Container) container;
        }catch (Throwable e){
            String errorMsg = "initialize container error,errMsg:" + e.getMessage();
            LOG.error(errorMsg, e);
            throw new ExecutorRuntimeException(errorMsg);

        }
    }

    private static Class<?> getClass(String className) throws ClassNotFoundException {
        Class<?> cls = CONTAINER_CLASS_MAP.get(className);
        if(cls == null){
           cls = Class.forName(className);
           CONTAINER_CLASS_MAP.put(className, cls);
        }
        return cls;
    }

    /**
     *
     * @param container
     * @return
     */
    public static boolean isAsyncConstainer(Container container){
        if(container != null && container instanceof AsyncContainer){
            return true;
        }
        return false;
    }
    /**
     *
     * @param container
     * @return
     */
    public static boolean isSyncConstainer(Container container){
        if(container != null && container instanceof SyncContainer){
            return true;
        }
        return false;
    }

    private static boolean isAssignableFrom(long containerId, Class cls){
        ExecutorContainerService executorContainerService = ExecutorServiceUtils.getExecutorContainerService();
        ExecutorContainer executorContainer = executorContainerService.getById(containerId);
        String className = executorContainer.getClassName();

        try {
            Class<?> containerClass = getClass(className);
            if(cls.isAssignableFrom(containerClass)){
                return true;
            }
        } catch (Throwable e) {
            LOG.error("get container class error", e);
        }
        return false;
    }
    /**
     * 容器是否是异步的
     * @param containerId
     * @return
     */
    public static boolean isAsync(long containerId){
        return isAssignableFrom(containerId, AsyncContainer.class);

    }
    /**
     * 容器是否是异步的
     * @param containerId
     * @return
     */
    public static boolean isSync(long containerId){
        return isAssignableFrom(containerId, SyncContainer.class);
    }

    /**
     * 是不是空任务
     * @param container
     * @return
     */
    public static boolean isNoopConstainer(Container container){
        if(container != null && container instanceof NoopContainer){
            return true;
        }
        return false;
    }

}
