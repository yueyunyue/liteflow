package cn.lite.flow.executor.common.consts;

import cn.lite.flow.common.model.consts.CommonConstants;

/**
 * @description: 常量
 * @author: yueyunyue
 * @create: 2018-08-22
 **/
public class Constants {

    public static final int NOOP_ID_RANDOM_SIZE = 6;

    /**
     * java
     */
    public static final String JAVA_JAR = "jar";

    public static final String JAVA_JAR_SUFFIX = CommonConstants.POINT + JAVA_JAR;            //jar包

    public static final String JAVA_CLASSPATH_SPLIT = CommonConstants.COLON;                  //classpath分隔符

    public static final String JAVA_MAIN_JAR_PATH = "mainJarPath";                            //主类所在jar路径

    public static final String JAVA_CLASSPATH = "classpath";                                  //java classath

    public static final String JAVA_GLOBAL_CLASSPATH = "globalClasspaths";                    //全局classpath

    public static final String JAVA_MAIN_CLASS = "mainClass";                                 //主类

    public static final String JAVA_INITIAL_MEMORY_SIZE = "Xms";                              //jvm堆参数

    public static final String JAVA_MAX_MEMORY_SIZE = "Xmx";                                  //jvm堆

    public static final String JAVA_MAIN_ARGS = "mainArgs";                                   //java运行参数

    public static final String JAVA_JVM_PARAMS = "jvmArgs";                                    //jvm参数

    public static final String JAVA_GLOBAL_JVM_PARAMS = "globalJvmArgs";                       //全局的jvm参数

    public static final String JAVA_DEFAULT_INITIAL_MEMORY_SIZE = "64M";                       //默认内存初始值

    public static final String JAVA_DEFAULT_MAX_MEMORY_SIZE = "256M";                          //默认内存最大值

    public static String JAVA_COMMAND = "java";

    public static String JAVA_COMMAND_CP = "-cp";

    /**
     * process
     */
    public final static String ENV_PREFIX = "env.";

    public final static String WORKING_DIR = "";

    public final static String ENV_PREFIX_UCASE = "";

    /**
     * shell
     */
    public static final String SHELL_COMMAND = "command";

    /**
     * 任务文件地址
     */
    public static final String CONFIG_FILE_SUFFIX = "_config.json";

    public static final String JOB_CONFIG_PATH = "jobConfigPath";

    /**
     * 查询相关
     */
    public static final int MAX_QUERY_COUNT = 50;                           //最大查询数量


    /**
     * 附件相关
     */
    public static final String ATTACHMENT_PREFIX = "attachment://executor";         //附件前缀

    /**
     * 任务消息常量
     */
    public static final String MSG_ERROR = "ERROR";             //异常

    public static final String MSG_SUCCESS = "SUCCESS";         //成功

    public static final String MSG_RUNNING = "RUNNING";         //运行中

    public static final String MSG_FAIL = "FAIL";               //失败

    /**
     * 日志
     */
    public static final String LOG_EMPTY = "empty";             //空
    /**
     * 进程
     */
    public static final String PRCESS_JOB_PREFIX = "p";    //空



}
