package cn.lite.flow.executor.service.impl;

import cn.lite.flow.console.client.service.ConsoleCallbackRpcService;
import cn.lite.flow.executor.dao.ExecutorCallbackMapper;
import cn.lite.flow.executor.model.basic.ExecutorCallback;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import cn.lite.flow.executor.model.consts.JobCallbackStatus;
import cn.lite.flow.executor.model.query.ExecutorCallbackQM;
import cn.lite.flow.executor.service.ExecutorCallbackService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-01-30
 **/
@Service("executorCallbackServiceImpl")
public class ExecutorCallbackServiceImpl implements ExecutorCallbackService {

    private final static Logger LOG = LoggerFactory.getLogger(ExecutorCallbackServiceImpl.class);

    @Resource
    private ExecutorCallbackMapper executorCallbackMapper;

    @Autowired
    private ConsoleCallbackRpcService consoleCallbackService;

    @Override
    public void add(ExecutorCallback model) {
        executorCallbackMapper.insert(model);
    }

    @Override
    public ExecutorCallback getById(long id) {
        return executorCallbackMapper.getById(id);
    }

    @Override
    public int update(ExecutorCallback model) {
        return executorCallbackMapper.update(model);
    }

    @Override
    public int count(ExecutorCallbackQM queryModel) {
        return executorCallbackMapper.count(queryModel);
    }

    @Override
    public List<ExecutorCallback> list(ExecutorCallbackQM queryModel) {
        return executorCallbackMapper.findList(queryModel);
    }

    @Override
    public boolean callback(long jobSourceId, int jobStatus, String msg) {

        ExecutorJobStatus status = ExecutorJobStatus.getType(jobStatus);
        try {
            switch (status) {
                case SUCCESS:
                    consoleCallbackService.success(jobSourceId);
                    break;
                case FAIL:
                    consoleCallbackService.fail(jobSourceId, msg);
                    break;
                case KILLED:
                    consoleCallbackService.killed(jobSourceId, msg);
                    break;
                case RUNNING:
                    consoleCallbackService.running(jobSourceId, msg);
                    break;
            }
            LOG.info("job callback success, jobSourceId:{}, status:{}", jobSourceId, jobStatus);
            return true;
        } catch (Throwable e) {
            LOG.error("job callback error, jobSourceId:" + jobSourceId, e);
        }
        return false;
    }

    @Override
    public boolean callbackById(long id) {
        ExecutorCallback callback = executorCallbackMapper.getById(id);
        boolean callbackStatus = this.callback(callback.getJobSourceId(), callback.getJobStatus(), callback.getJobMsg());
        if(callbackStatus){
            this.callbackSuccess(id);
        }
        return callbackStatus;
    }

    @Override
    public int callbackSuccess(long id) {
        ExecutorCallback executorCallback = new ExecutorCallback();
        executorCallback.setId(id);
        executorCallback.setStatus(JobCallbackStatus.DONE.getValue());
        return executorCallbackMapper.update(executorCallback);
    }

    @Override
    public void ignore(long id) {
        this.batchIgnore(Lists.newArrayList(id));
    }

    @Override
    public void batchIgnore(List<Long> ids) {
        executorCallbackMapper.batchUpdateStatus(ids, JobCallbackStatus.NO_NEED.getValue());
    }

    @Override
    public void ignoreCallbackStatusOfJob(long jobId) {

        ExecutorCallbackQM qm = new ExecutorCallbackQM();
        qm.setJobId(jobId);
        qm.setStatus(JobCallbackStatus.WAITING.getValue());
        List<ExecutorCallback> callbacks = executorCallbackMapper.findList(qm);
        if(CollectionUtils.isNotEmpty(callbacks)){
            List<Long> ids = callbacks.stream().map(ExecutorCallback::getId).collect(Collectors.toList());
            this.batchIgnore(ids);
        }

    }

}
