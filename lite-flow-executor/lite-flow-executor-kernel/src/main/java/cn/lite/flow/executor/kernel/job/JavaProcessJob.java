package cn.lite.flow.executor.kernel.job;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.utils.Props;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: Java 进程任务
 * @author: yueyunyue
 * @create: 2018-08-22
 **/
public class JavaProcessJob extends ProcessJob {

    public JavaProcessJob(long executorJobId, Props sysProps, Props jobProps, Logger log) {
        super(executorJobId, sysProps, jobProps, log);
    }

    @Override
    protected List<String> getCommandList() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(this.createJavaCommandLine());
        return list;
    }

    protected String createJavaCommandLine() {
        StringBuilder command = new StringBuilder();
        command.append(Constants.JAVA_COMMAND).append(CommonConstants.BLANK_SPACE);
        command.append(this.getJVMArguments()).append(CommonConstants.BLANK_SPACE);
        command.append(Constants.JAVA_COMMAND_CP).append(CommonConstants.BLANK_SPACE).append(this.createArguments(this.getClassPaths(), Constants.JAVA_CLASSPATH_SPLIT)).append(CommonConstants.BLANK_SPACE);
        command.append(this.getJavaClass()).append(CommonConstants.BLANK_SPACE);
        command.append(this.getMainArguments()).append(CommonConstants.BLANK_SPACE);
        String commandStr = command.toString();
        logger.info("create java command: " + commandStr);
        return commandStr;
    }

    protected List<String> getClassPaths() {

        final List<String> classPaths = jobProps.getStringList(Constants.JAVA_CLASSPATH, null, CommonConstants.COMMA);

        final ArrayList<String> classpathList = new ArrayList<>();
        /**
         * 添加全局的classpath，一些通用的jar包
         */
        if (jobProps.containsKey(Constants.JAVA_GLOBAL_CLASSPATH)) {
            final List<String> globalClasspath =
                    jobProps.getStringList(Constants.JAVA_GLOBAL_CLASSPATH);
            for (final String global : globalClasspath) {
                logger.info("add to global classpath:" + global);
                classpathList.add(global);
            }
        }

        if (classPaths == null) {
            String mainJarPath = getPath();
            final File path = new File(mainJarPath);
            logger.info("load jar path {}" ,path);

            /**
             * 1.文件夹，把所有jar都添加进来
             * 2.如果是文件，并且是jar，直接添加
             */
            if (path != null) {
                if(path.isDirectory()){
                    for (final File file : path.listFiles()) {
                        if (StringUtils.endsWith(file.getName(), Constants.JAVA_JAR_SUFFIX)) {
                            String jarAbsolutePath = file.getAbsolutePath();
                            logger.info("add to classpath:" + jarAbsolutePath);
                            classpathList.add(jarAbsolutePath);
                        }
                    }
                }else if(StringUtils.endsWith(path.getName(), Constants.JAVA_JAR_SUFFIX)) {
                    classpathList.add(path.getAbsolutePath());
                }

            }
        } else {
            classpathList.addAll(classPaths);
        }
        return classpathList;
    }

    protected String createArguments(final List<String> arguments, final String separator) {
        if (arguments != null && arguments.size() > 0) {
            String param = "";
            for (final String arg : arguments) {
                param += arg + separator;
            }
            return param.substring(0, param.length() - 1);
        }
        return "";
    }

    protected String getJavaClass() {
        return jobProps.getString(Constants.JAVA_MAIN_CLASS);
    }

    /**
     * 获取jvm堆最小数
     * @return
     */
    protected String getInitialMemorySize() {
        return jobProps.getString(Constants.JAVA_INITIAL_MEMORY_SIZE,
                Constants.JAVA_DEFAULT_INITIAL_MEMORY_SIZE);
    }

    protected String getMaxMemorySize() {
        return jobProps.getString(Constants.JAVA_MAX_MEMORY_SIZE, Constants.JAVA_DEFAULT_MAX_MEMORY_SIZE);
    }

    /**
     * 获取运行参数
     * @return
     */
    protected String getMainArguments() {
        return jobProps.getString(Constants.JAVA_MAIN_ARGS, "");
    }

    /**
     * 获取jvm参数
     * @return
     */
    protected String getJVMArguments() {
        final String globalJVMArgs = jobProps.getString(Constants.JAVA_GLOBAL_JVM_PARAMS, null);

        if (globalJVMArgs == null) {
            return jobProps.getString(Constants.JAVA_JVM_PARAMS, "");
        }

        return globalJVMArgs + CommonConstants.BLANK_SPACE + jobProps.getString(Constants.JAVA_JVM_PARAMS, "");
    }

    /**
     * 获取main函数所在jar的路径
     * @return
     */
    private String getPath(){
        String mainJarPath = jobProps.getString(Constants.JAVA_MAIN_JAR_PATH, "");
        return mainJarPath;
    }
}
