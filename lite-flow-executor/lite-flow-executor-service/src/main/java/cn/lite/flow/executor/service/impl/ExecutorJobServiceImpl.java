package cn.lite.flow.executor.service.impl;

import cn.lite.flow.common.utils.DateUtils;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.ContainerMetadata;
import cn.lite.flow.executor.dao.ExecutorJobMapper;
import cn.lite.flow.executor.model.basic.ExecutorCallback;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import cn.lite.flow.executor.model.consts.JobCallbackStatus;
import cn.lite.flow.executor.model.kernel.Container;
import cn.lite.flow.executor.model.query.ExecutorJobQM;
import cn.lite.flow.executor.service.ExecutorCallbackService;
import cn.lite.flow.executor.service.ExecutorJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2018-09-13
 **/
@Service("executorJobServiceImpl")
public class ExecutorJobServiceImpl implements ExecutorJobService {

    private final static Logger LOG = LoggerFactory.getLogger(ExecutorJobServiceImpl.class);

    @Resource
    private ExecutorJobMapper executorJobMapper;

    @Autowired
    private ExecutorCallbackService executorCallbackService;


    @Override
    public ExecutorJob getById(long id) {
        return executorJobMapper.getById(id);
    }

    @Override
    public void bindApplicationId(long jobId, String applicationId) {
        executorJobMapper.bindApplicationId(jobId, applicationId);
    }

    @Override
    public void bindApplicationIdAndRun(long jobId, String applicationId) {
        executorJobMapper.bindApplicationIdWithStatus(jobId, applicationId, ExecutorJobStatus.SUCCESS.getValue());
    }

    @Transactional("executorTxManager")
    @Override
    public void success(long jobId) {

        ExecutorJob executorJob = new ExecutorJob();
        executorJob.setId(jobId);
        executorJob.setStatus(ExecutorJobStatus.SUCCESS.getValue());
        executorJob.setMsg(ExecutorJobStatus.SUCCESS.name());
        Date now = DateUtils.getNow();
        executorJob.setEndTime(now);
        executorJobMapper.update(executorJob);

        //移除相关容器
        ContainerMetadata.removeContainer(jobId);
        this.callback(jobId);
        LOG.info("job success,id:{}", jobId);
    }

    @Transactional("executorTxManager")
    @Override
    public void fail(long jobId, String errorMsg) {

        ExecutorJob job = new ExecutorJob();
        job.setId(jobId);
        job.setStatus(ExecutorJobStatus.FAIL.getValue());
        job.setMsg(errorMsg);
        Date now = DateUtils.getNow();
        job.setEndTime(now);
        executorJobMapper.update(job);
        //移除相关容器
        ContainerMetadata.removeContainer(jobId);
        this.callback(jobId);
        LOG.info("job fail,id:{}", jobId);
    }
    @Override
    public boolean callback(long jobId) {
        ExecutorJob executorJob = executorJobMapper.getById(jobId);
        boolean callback = executorCallbackService.callback(executorJob.getSourceId(), executorJob.getStatus(), executorJob.getMsg());
        if(!callback){
            ExecutorCallback executorCallback = new ExecutorCallback();
            executorCallback.setJobId(executorJob.getId());
            executorCallback.setJobSourceId(executorJob.getSourceId());
            executorCallback.setJobStatus(executorJob.getStatus());
            executorCallback.setStatus(JobCallbackStatus.WAITING.getValue());
            executorCallback.setJobMsg(executorJob.getMsg());
            executorCallback.setExecutorServerId(executorJob.getExecutorServerId());
            executorCallbackService.add(executorCallback);
        }
        return callback;
    }

    @Override
    public void run(long jobId) {
        ExecutorJob executorJob = new ExecutorJob();
        executorJob.setId(jobId);
        Date now = DateUtils.getNow();
        executorJob.setStartTime(now);
        executorJob.setStatus(ExecutorJobStatus.RUNNING.getValue());
        executorJob.setMsg(ExecutorJobStatus.RUNNING.name());
        executorJobMapper.update(executorJob);
        this.callback(jobId);
    }

    @Transactional("executorTxManager")
    @Override
    public void kill(long id) {
        this.kill(id, true);
    }

    @Override
    @Transactional("executorTxManager")
    public void kill(long id, boolean isCallback) {
        ExecutorJob executorJob = executorJobMapper.getById(id);
        if (executorJob == null) {
            throw new ExecutorRuntimeException("该任务不存在");
        }

        try {
            Container container = ContainerMetadata.getContainer(id);
            if (container != null) {
                container.kill();
            }
        } catch (Throwable e) {
            LOG.error("job kill error, id:{}", id, e);
            throw new ExecutorRuntimeException("操作失败");
        }

        ExecutorJob job = new ExecutorJob();
        job.setId(id);
        job.setStatus(ExecutorJobStatus.KILLED.getValue());
        Date now = DateUtils.getNow();
        job.setEndTime(now);
        executorJob.setMsg(ExecutorJobStatus.KILLED.name());

        executorJobMapper.update(executorJob);
        //移除相关容器
        ContainerMetadata.removeContainer(id);

        /**
         * kill掉后，之前的回调全部忽略
         */
        executorCallbackService.ignoreCallbackStatusOfJob(id);

        /**
         * 回调console
         */
        if(isCallback){
            this.callback(id);
        }
        LOG.info("job kill, id:{}, status", id);
    }

    @Override
    public ExecutorJob getBySourceId(long sourceId) {
        return executorJobMapper.getBySourceId(sourceId);
    }

    @Override
    public void add(ExecutorJob executorJob) {
        executorJobMapper.insert(executorJob);
    }

    @Override
    public int update(ExecutorJob executorJob) {
        return executorJobMapper.update(executorJob);
    }

    @Override
    public int count(ExecutorJobQM queryModel) {
        return executorJobMapper.count(queryModel);
    }

    @Override
    public List<ExecutorJob> list(ExecutorJobQM queryModel) {
        return executorJobMapper.findList(queryModel);
    }
}
