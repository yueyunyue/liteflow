package cn.lite.flow.common.utils;

import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/**
 * @description:
 * @author: yarn相关工具
 * @create: 2019-04-25
 **/
public class YarnClientHolder {

    private static volatile YarnClient YARN_CIENT;

    public static YarnClient getYarnClient(){

        if(YARN_CIENT == null){
            synchronized (YarnClientHolder.class){
                YARN_CIENT = YarnClient.createYarnClient();
                YARN_CIENT.init(new YarnConfiguration());
                YARN_CIENT.start();
            }
        }
        return YARN_CIENT;
    }

}
