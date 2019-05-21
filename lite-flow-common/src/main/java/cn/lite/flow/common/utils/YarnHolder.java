package cn.lite.flow.common.utils;

import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/**
 * @description: yarn相关工具
 * @author: yueyunyue
 * @create: 2019-04-25
 **/
public class YarnHolder {

    private static volatile YarnClient YARN_CIENT;

    private static YarnConfiguration YARN_CONFIGURATION = new YarnConfiguration();

    public static YarnClient getYarnClient(){

        if(YARN_CIENT == null){
            synchronized (YarnHolder.class){
                YARN_CIENT = YarnClient.createYarnClient();
                YARN_CIENT.init(YARN_CONFIGURATION);
                YARN_CIENT.start();
            }
        }
        return YARN_CIENT;
    }

    public static YarnConfiguration getYarnConfiguration() {
        return YARN_CONFIGURATION;
    }
}
