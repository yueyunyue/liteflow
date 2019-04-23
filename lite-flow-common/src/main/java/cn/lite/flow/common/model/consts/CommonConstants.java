package cn.lite.flow.common.model.consts;

import org.springframework.stereotype.Component;

/**
 * 常量
 */
@Component
public class CommonConstants {

    /**
     * 常见字符
     */
    public static final String BLANK_SPACE = " ";                                             //空格

    public static final String COMMA = ",";                                                   //逗号

    public static final String POINT = ".";                                                   //句号

    public static final String COLON = ":";                                                   //冒号

    public static final String SEMICOLON = ";";                                               //分号

    public static final String LINE = "-";                                                    //横线

    public static final String DOUBLE_LINE = "--";                                            //双横线

    public static final String UNDERLINE = "_";                                               //下划线

    public static final String FILE_SPLIT = "/";                                              //文件分隔符

    public static final String EQUAL = "=";                                                   //等号

    /**
     * url相关
     */
    public static final String URL_QUESTION = "?";                                            //问号

    public static final String URL_PARAM_SPLIT = "&";                                         //url参数间分隔符

    public static final String URL_PROTOCOL_SPLIT = "://";                                    //url协议分隔符

    /**
     * db每批获取数量
     */
    public final static int LIST_BATCH_SIZE = 100;

    /**
     * 常用参数常量
     */
    public final static String PARAM_ID = "id";                                              //id

    public final static String PARAM_NAME = "name";                                          //名称

    public final static String PARAM_CONTAINER = "container";                                //容器

    public final static String PARAM_PLUGIN = "plugin";                                      //插件

    public final static String PARAM_FIELD_CONFIG = "fieldConfig";                           //字段配置

    public final static String PARAM_EXECUTOR_JOB_NAME = "executorJobName";                  //执行者任务名



}
