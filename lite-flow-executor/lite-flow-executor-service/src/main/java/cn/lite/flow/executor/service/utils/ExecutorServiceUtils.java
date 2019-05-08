package cn.lite.flow.executor.service.utils;

import cn.lite.flow.common.utils.SpringUtils;
import cn.lite.flow.executor.service.ExecutorAttachmentService;
import cn.lite.flow.executor.service.ExecutorContainerService;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.ExecutorPluginService;
import cn.lite.flow.executor.service.ExecutorServerService;

/**
 * @description: job工具
 * @author: yueyunyue
 * @create: 2018-09-13
 **/
public class ExecutorServiceUtils {
    /**
     * 获取job service
     * @return
     */
    public static ExecutorJobService getExecutorJobService(){
        ExecutorJobService executorJobService = SpringUtils.getBean(ExecutorJobService.class);
        return executorJobService;
    }

    /**
     * 获取container service
     * @return
     */
    public static ExecutorContainerService getExecutorContainerService(){
        ExecutorContainerService containerService = SpringUtils.getBean(ExecutorContainerService.class);
        return containerService;
    }

    /**
     * 获取plugin service
     * @return
     */
    public static ExecutorPluginService getExecutorPluginService(){
        ExecutorPluginService containerService = SpringUtils.getBean(ExecutorPluginService.class);
        return containerService;
    }
    /**
     * 获取server service
     * @return
     */
    public static ExecutorServerService getExecutorServerService(){
        ExecutorServerService containerService = SpringUtils.getBean(ExecutorServerService.class);
        return containerService;
    }

    /**
     * 获取附件
     * @return
     */
    public static ExecutorAttachmentService getExecutorAttachmentService(){
        ExecutorAttachmentService attachmentService = SpringUtils.getBean(ExecutorAttachmentService.class);
        return attachmentService;
    }


}
