package cn.lite.flow.executor.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 任务logger工厂
 * @author: yueyunyue
 * @create: 2019-01-25
 **/
public class ExecutorLoggerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorLoggerFactory.class);

    private static final String LOG_FILE_SUFFIX = ".log";

    private static final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

    private static final Configuration config = ctx.getConfiguration();

    private static final String LAYOUT_TEMPLATE = "%d{HH:mm:ss.SSS} %-5level - %msg%n";

    public static final String JOB_LOGGER_PREFIX = "executeJob-";

    public static final String JOB_LOGGER_FILE_PREFIX = "job-";

    /**
     * appender配置
     */
    private static final String APPRENDER_REF = "File";

    private static final Level APPRENDER_LEVEL = Level.ALL;

    private static final Filter APPRENDER_FILTER = null;

    /**
     * LOGGER 配置
     */
    private static final boolean LOGGER_ADDITIVITY = false;

    private static final String LOGGER_INCLUDE_LOCATIOIN = "true";

    private static final Property[] LOGGER_PROPERTY = null;



    private ExecutorLoggerFactory(){}

    /**
     * 获取任务的
     * @param jobId
     * @return
     */
    public static String getJobLoggerName(long jobId){
        return JOB_LOGGER_PREFIX + jobId;
    }
    public static String getJobLogFileName(long jobId){
        return JOB_LOGGER_FILE_PREFIX + jobId + LOG_FILE_SUFFIX;
    }

    /**
     * 创建并启动一个的logger
     */
    private static void start(String loggerName, String logPath) {
        /**
         * 设置layout
         */
        Layout layout = PatternLayout
                .newBuilder()
                .withConfiguration(config)
                .withPattern(LAYOUT_TEMPLATE)
                .build();

        String fileName = logPath;
        /**
         * 设置appender
         */
        FileAppender fileAppender = FileAppender.newBuilder()
                .withName(loggerName)
                .withFileName(fileName)
                .withLayout(layout)
                .build();

        AppenderRef appenderRef = AppenderRef.createAppenderRef(APPRENDER_REF, APPRENDER_LEVEL, APPRENDER_FILTER);
        LoggerConfig loggerConfig = LoggerConfig.createLogger(
                LOGGER_ADDITIVITY,
                Level.ALL,
                loggerName,
                LOGGER_INCLUDE_LOCATIOIN,
                new AppenderRef[] {appenderRef},
                LOGGER_PROPERTY,
                config,
                APPRENDER_FILTER
        );
        loggerConfig.addAppender(fileAppender, APPRENDER_LEVEL, APPRENDER_FILTER);
        /**
         * 添加appender
         */
        config.addLogger(loggerName, loggerConfig);
        ctx.updateLoggers();
        LOG.info("create logger {}, path is {}", loggerName, logPath);
        /**
         * start
         */
        loggerConfig.start();
    }

    /**
     * 使用完之后记得调用此方法关闭动态创建的logger，避免内存不够用或者文件打开太多*
     */
    public static void stop(String loggerName) {
        try {
            synchronized (config){
                if (config!= null && config.getLoggers().containsKey(loggerName)) {
                    config.removeLogger(loggerName);
                    config.getLoggerConfig(loggerName).removeAppender(loggerName);
//                    config.getAppender(loggerName).stop();
                    ctx.updateLoggers();
                    LOG.info("remove logger {}", loggerName);
                }
            }
        }catch (Throwable e){
            LOG.error("remove logger {} error", loggerName, e);
        }

    }

    public static void stop(long jobId) {
       String loggerName = getJobLoggerName(jobId);
       stop(loggerName);

    }

    /**
     * 获取日志的全路径
     * @param workspace
     * @param jobId
     * @return
     */
    public static String getLogFile(String workspace, long jobId) {
        return workspace + CommonConstants.FILE_SPLIT + getJobLogFileName(jobId);
    }

    /**
     * 获取Logger
     */
    public static org.slf4j.Logger getLogger(long jobId, String jobWorkspace) {
        String loggerName = getJobLoggerName(jobId);
        return getLogger(loggerName, getLogFile(jobWorkspace, jobId));
    }

    public static org.slf4j.Logger getLogger(String loggerName, String logPath) {
        synchronized (config) {
            if (!config.getLoggers().containsKey(loggerName)) {
                start(loggerName, logPath);
            }
        }
        Logger logger = LoggerFactory.getLogger(loggerName);
        return logger;
    }
}
