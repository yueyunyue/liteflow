package cn.lite.flow.executor.model.kernel;

/**
 * 基础container
 */
public interface Container {

  /**
   * 运行方法
   * @throws Exception
   */
  void run() throws Exception;

  /**
   * 取消任务
   * @throws Exception
   */
  void kill() throws Exception;

  /**
   * 任务是否已经取消
   * @return
   */
  boolean isFailed();

  /**
   * 是否已经成功
   * @return
   */
  boolean isSuccess();

  /**
   * 是否运行中
   * @return
   */
  boolean isRunning();

  /**
   * 是否最终状态
   * @return
   */
  boolean isFinish();


}
