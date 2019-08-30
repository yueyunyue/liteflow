package cn.lite.flow.common.model.consts;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Set;

/**
 * 常量
 */
@Component
public class CommonConstants {

    /**
     * 普通
     */
    public final static int ZERO = 0;

    public final static int FIFTY_NINE = 59;

    public static final String UTF8 = "UTF-8";                                               //UTF-8

    public static final Charset UTF8_CHARSET =  Charset.forName(CommonConstants.UTF8);       //UTF-8

    /**
     * java 进程退出
     */
    public static final int SYSTEM_EXIT_SUCCESS = 0;

    public static final int SYSTEM_EXIT_ERROR = -1;
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

    public final static String PARAM_CONSOLE_TASK_VERSION = "consoleTaskVersionNo";         //任务对应的版本号

    public final static String PARAM_FILE = "file";                                          //文件

    public final static String PARAM = "param";                                              //参数

    public final static String PARAM_CONTENT = "content";                                    //内容

    public final static String PARAM_SQL = "sql";                                            //sql

    public final static String PARAM_TYPE = "type";                                          //类型

    public final static String PARAM_USER = "user";                                          //用户

    public final static String PARAM_PASSWORD = "password";                                  //类型

    public final static String PARAM_DB = "database";                                        //数据库

    public final static String PARAM_IP = "ip";                                              //ip

    public final static String PARAM_PORT = "port";                                          //端口号

    public final static String PARAM_QUEUE = "queue";                                        //队列

    /**
     * spark相关参数
     */
    public final static String SPARK_PARAM_YARN_QUEUE = PARAM_QUEUE;                         //yarn队列

    public final static String SPARK_PARAM_YARN_MAIN_CLASS = "mainClass";                    //main方法所在类

    public final static String SPARK_PARAM_YARN_MAIN_JAR = "mainJar";                        //mainClass所在jar

    public final static String SPARK_PARAM_YARN_DEPENDENCY_JAR = "dependencyJars";           //任务依赖的jar包

    public final static String SPARK_PARAM_INSTANCE_NUM = "instanceNum";                     //实例数量

    public final static String SPARK_PARAM_DRIVER_CORES = "driverCore";                      //driver核数

    public final static String SPARK_PARAM_DRIVER_MEMORY = "driverMemory";                   //driver内存

    public final static String SPARK_PARAM_EXECUTOR_CORES = "executorCore";                  //executor核数

    public final static String SPARK_PARAM_EXECUTOR_MEMORY = "executorMemory";               //executor内存

    public final static String SPARK_PARAM_MEMORY_UNIT = "m";                                //spark内存单位


    /**
     * 文本类型文件后缀
     */
    public final static Set<String> TEXT_FILE_SUFFIX = ImmutableSet.of(".txt", ".sh", ".sql", ".json");

    /**
     * 附件相关
     */
    public static final String EXECUTOR = "executor";                                         //executor

    public static final String ATTACHMENT_PREFIX = "liteAttachment://";                       //附件前缀

    public static final String HDFS_PREFIX = "hdfs://";                                       //hdfs前缀
    /**
     * http相关参数
     */
    public static final String HTTP_URL = "url";                                              //url路径

    public static final String HTTP_CHECK_URL = "checkUrl";                                   //异步http，校验

    public static final String HTTP_METHOD = "method";                                        //executor

    public static final String HTTP_PARAM = "param";                                          //参数

    public static final String HTTP_HEADER = "header";                                        //header参数

    public static final String HTTP_READ_TIMEOUT = "readTimeOut";                             //参数

    public static final long DEFAULT_HTTP_READ_TIMEOUT =  5000;                                //默认读取超时时间


}
