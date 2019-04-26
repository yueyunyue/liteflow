package cn.lite.flow.executor.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 进程相关工具
 * @author: yueyunyue
 * @create: 2019-03-12
 **/
public class ProcessorUtils {

    /**
     * 生成进程 应用id
     * @param executorJobId
     * @param processId
     * @return
     */
    public static String generateApplicationId(long executorJobId, long processId){
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.PROCESS_JOB_PREFIX);
        builder.append(CommonConstants.LINE);
        builder.append(executorJobId);
        builder.append(CommonConstants.LINE);
        builder.append(processId);
        return builder.toString();
    }

    /**
     * 通过应用id获取进程id
     * @param applicationId
     * @return
     */
    public static Long getProcessId(String applicationId){
        if(StringUtils.isBlank(applicationId)){
            return null;
        }
        String[] array = StringUtils.split(applicationId, CommonConstants.LINE);
        if(array.length == 3){
            long id = Long.parseLong(array[2]);
            return id;
        }

        return null;
    }

}
