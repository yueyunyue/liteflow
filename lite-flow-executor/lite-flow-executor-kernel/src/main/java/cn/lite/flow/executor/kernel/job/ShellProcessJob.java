package cn.lite.flow.executor.kernel.job;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.utils.Props;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description: shell任务
 * @author: yueyunyue
 * @create: 2018-08-24
 **/
public class ShellProcessJob extends ProcessJob {

    public ShellProcessJob(long executorJobId, Props sysProps, Props jobProps, Logger log) {
        super(executorJobId, sysProps, jobProps, log);
    }

    @Override
    protected List<String> getCommandList(){
        StringBuilder paramBuild = new StringBuilder();
        String scriptPath = jobProps.getString(Constants.SHELL_SCRIPT_PATH);
        paramBuild.append(Constants.SHELL_COMMAND).append(CommonConstants.BLANK_SPACE).append(scriptPath);
        String shellCommand = paramBuild.toString();
        this.logger.info("job:{} shell command:{}", this.executorJobId, shellCommand);
        return Lists.newArrayList(shellCommand);
    }

}
