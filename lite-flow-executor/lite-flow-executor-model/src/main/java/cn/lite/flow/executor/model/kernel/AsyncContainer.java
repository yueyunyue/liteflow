package cn.lite.flow.executor.model.kernel;

import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;

/**
 * 异步容器
 */
public abstract class AsyncContainer extends AbstractContainer{

  public AsyncContainer(ExecutorJob executorJob) {
    super(executorJob);
    /**
     * 异步任务是否是运行中
     */
    if(executorJob.getStatus() == ExecutorJobStatus.RUNNING.getValue()){
      super.setStatus(ContainerStatus.RUNNING);
    }
  }

  public abstract void checkStatus();

}
