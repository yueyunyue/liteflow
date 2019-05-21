package cn.lite.flow.console.common.consts;

import cn.lite.flow.common.model.consts.CommonConstants;

/**
 *通用常量
 */
public class Constants {

    /**
     * 参数
     */
    public final static String PARAM_VARIABLE_SPLIT = ",";                           //参数分隔符,


    /**
     * 时间相关
     */
    public final static String START_TIME = "startTime";                               //开始时间

    public final static String END_TIME = "endTime";                                   //结束时间


    /**
     * 重试相关
     */
    public final static String RETRY_NUM = "retryNum";                                 //重试次数

    public final static String RETRY_PERIOD = "retryPeriod";                           //重试周期

    public final static String PARAM_FIELD_CONFIG = "fieldConfig";                     //插件、容器参数配置

    public final static String PARAM_PLUGIN_CONF = "pluginConf";                       //插件参数

    public final static String PARAM_PLUGIN_CONF_FIELD_PREFIX = PARAM_PLUGIN_CONF + CommonConstants.POINT;   //插件参数前缀

    public final static String PARAM_NAME = "name";                                    //名字

    public final static String PARAM_MSG = "msg";                                      //信息



}
