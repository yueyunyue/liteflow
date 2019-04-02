package cn.lite.flow.executor.model.kernel;

import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;

/**
 * @description: 抽象container
 * @author: yueyunyue
 * @create: 2018-08-16
 **/
public abstract class AbstractContainer implements Container {

    protected final ExecutorJob executorJob;

    protected volatile int status = ContainerStatus.NEW.getValue();

    public AbstractContainer(ExecutorJob executorJob) {
        this.executorJob = executorJob;
    }

    public ExecutorJob getExecutorJob() {
        return executorJob;
    }

    protected void setStatus(ContainerStatus containerStatus){
        this.status = containerStatus.getValue();
    }

    public boolean isRunning() {
        return this.status == ContainerStatus.RUNNING.getValue();
    }

    public boolean isFailed() {
        return this.status == ContainerStatus.FAIL.getValue();
    }

    public boolean isSuccess() {
        return this.status == ContainerStatus.SUCCESS.getValue();
    }

    public boolean isFinish() {
        return isSuccess() || isFailed();
    }
}
