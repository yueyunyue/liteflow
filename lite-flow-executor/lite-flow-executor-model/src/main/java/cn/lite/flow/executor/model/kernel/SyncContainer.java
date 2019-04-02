package cn.lite.flow.executor.model.kernel;

import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ContainerStatus;

/**
 * 同步容器
 */
public abstract class SyncContainer extends AbstractContainer {

  public SyncContainer(ExecutorJob executorJob) {
    super(executorJob);
  }

  protected abstract void runInternal() throws Exception;

  public void run() throws Exception {
    if(this.isRunning()){
      return;
    }
    super.setStatus(ContainerStatus.RUNNING);

    runInternal();

  }

}
