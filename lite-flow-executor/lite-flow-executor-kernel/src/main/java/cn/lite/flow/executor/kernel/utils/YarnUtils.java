package cn.lite.flow.executor.kernel.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: yarn相关工具
 * @author: cyp
 * @create: 2019-04-15
 **/
public class YarnUtils {

    private volatile static YarnClient yarnClient;

    public final Logger LOG = LoggerFactory.getLogger(YarnUtils.class);

    /**
     * 转换为ApplicationId
     * @param appId
     * @return
     */
   public static ApplicationId convertApplicationId(String appId){
       if(StringUtils.isBlank(appId)){
           return null;
       }
       ApplicationId applicationId = ConverterUtils.toApplicationId(appId);
       return applicationId;
   }




}
