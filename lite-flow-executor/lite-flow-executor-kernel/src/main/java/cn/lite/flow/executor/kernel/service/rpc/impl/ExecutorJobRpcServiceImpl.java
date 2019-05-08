package cn.lite.flow.executor.kernel.service.rpc.impl;

import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.common.utils.JSONUtils;
import cn.lite.flow.executor.client.ExecutorJobRpcService;
import cn.lite.flow.executor.client.model.JobParam;
import cn.lite.flow.executor.client.model.SubmitExecuteJob;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.utils.ContainerMetadata;
import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.kernel.conf.ExecutorMetadata;
import cn.lite.flow.executor.kernel.container.ContainerFactory;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.basic.ExecutorPlugin;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import cn.lite.flow.executor.model.kernel.Container;
import cn.lite.flow.executor.model.query.ExecutorJobQM;
import cn.lite.flow.executor.model.query.ExecutorServerQM;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.ExecutorPluginService;
import cn.lite.flow.executor.service.utils.ExecutorServiceUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @description: job Rpc相关
 * @author: yueyunyue
 * @create: 2019-01-14
 **/
@Service("executorJobRpcServiceImpl")
public class ExecutorJobRpcServiceImpl implements ExecutorJobRpcService {

    private final static Logger LOG = LoggerFactory.getLogger(ExecutorJobRpcServiceImpl.class);

    @Autowired
    private ExecutorJobService executorJobService;

    @Autowired
    private ExecutorPluginService pluginService;

    @Override
    public Long submitJob(SubmitExecuteJob submitExecuteJob) {

        /**
         * 任务已经存在的话，直接返回
         */
        ExecutorJob executorJob = executorJobService.getBySourceId(submitExecuteJob.getInstanceId());
        if(executorJob != null){
            return executorJob.getId();
        }

        ExecutorJob job = new ExecutorJob();
        Long pluginId = submitExecuteJob.getPluginId();
        /**
         * 获取插件信息
         */
        ExecutorPlugin plugin = pluginService.getById(pluginId);
        String jobPluginConf = submitExecuteJob.getPluginConf();
        String pluginConfig = plugin.getConfig();
        /**
         * 合并配置
         */
        String config = mergeConfig(pluginConfig, jobPluginConf);

        job.setPluginId(pluginId);
        job.setConfig(config);
        job.setContainerId(plugin.getContainerId());
        job.setExecutorServerId(ExecutorMetadata.getServerId());

        job.setSourceId(submitExecuteJob.getInstanceId());
        job.setStatus(ExecutorJobStatus.NEW.getValue());

        executorJobService.add(job);
        /**
         * 添加任务
         */
        try {
            Container container = ContainerFactory.newInstance(job);
            ContainerMetadata.putContainer(job.getId(), container);
        }catch (Throwable e){
            executorJobService.fail(job.getId(), e.getMessage());
        }

        return job.getId();
    }

    /**
     * 将插件中的配置以及任务的配置合并，以便在容器中运行
     * @param pluginConf
     * @param jobPluginConfig
     * @return
     */
    private String mergeConfig(String pluginConf, String jobPluginConfig){

        if(StringUtils.isBlank(pluginConf)){
            return jobPluginConfig;
        }
        if(StringUtils.isBlank(jobPluginConfig)){
            return pluginConf;
        }

        JSONObject jobPluginConfigObj = JSONObject.parseObject(jobPluginConfig);
        JSONObject pluginConfigObj = JSONObject.parseObject(pluginConf);
        jobPluginConfigObj.putAll(pluginConfigObj);

        return JSONUtils.toJSONStringWithoutCircleDetect(jobPluginConfigObj);
    }

    @Override
    public ExecutorJob getById(long id) {
        return executorJobService.getById(id);
    }

    @Override
    public List<ExecutorJob> list(JobParam jobParam) {
        ExecutorJobQM qm = getExecutorJobQM(jobParam);
        qm.setId(jobParam.getId());
        qm.setApplicationId(jobParam.getApplicationId());
        qm.addOrderDesc(ExecutorServerQM.COL_ID);
        return executorJobService.list(qm);
    }

    private ExecutorJobQM getExecutorJobQM(JobParam jobParam) {
        ExecutorJobQM qm = new ExecutorJobQM();
        qm.setId(jobParam.getId());
        qm.setApplicationId(jobParam.getApplicationId());
        qm.setStatus(jobParam.getStatus());
        qm.setPage(Page.getPageByPageNo(jobParam.getPageNum(), jobParam.getPageSize()));
        return qm;
    }

    @Override
    public int count(JobParam jobParam) {
        ExecutorJobQM qm = getExecutorJobQM(jobParam);
        return executorJobService.count(qm);
    }


    @Override
    public void kill(long id) {
        executorJobService.kill(id);
    }

    @Override
    public void kill(long id, boolean isCallback) {
        executorJobService.kill(id, isCallback);
    }

    @Override
    public void callback(long id) {
        executorJobService.callback(id);
    }

    @Override
    public String getLog(long id) {
        String jobWorkspace = ExecutorMetadata.getJobWorkspace(id);
         String logFile = ExecutorLoggerFactory.getLogFile(jobWorkspace, id);
        File file = new File(logFile);
        if(file.exists()){
            try {
                String result = FileUtils.readFileToString(file, "utf-8");
                return result;
            } catch (IOException e) {
                LOG.error("read log error, jobId:{}", id, e);
            }
        }
        return Constants.LOG_EMPTY;
    }
}
