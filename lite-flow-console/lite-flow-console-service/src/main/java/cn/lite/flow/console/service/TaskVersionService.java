package cn.lite.flow.console.service;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.model.query.TaskVersionQM;

import java.util.Date;
import java.util.List;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskVersionService extends BaseService<TaskVersion, TaskVersionQM> {

    /**
     * 乐观锁
     * @param taskVersion
     * @param status
     * @return
     */
    int updateWithStatus(TaskVersion taskVersion, int status);

    /**
     * 通过任务id和版本获取数据
     * @param taskId
     * @param taskVersion
     * @return
     */
    TaskVersion getTaskVersion(long taskId, long taskVersion);

    /**
     * 获取最新状态
     * @param taskVersionId
     * @return
     */
    TaskInstance getLatestInstance(long taskVersionId);

    /**
     * kill掉当前任务实例
     * @param taskVersion
     * @return
     */
    boolean kill(long taskVersion);

    /**
     * kill掉当前任务实例
     * @param taskVersion
     * @param isExecutorCallback  执行者是否回调
     * @return
     */
    boolean kill(long taskVersion, boolean isExecutorCallback);

    /**
     * 修复
     * @param taskVersionId
     * @return
     */
    boolean fix(long taskVersionId);

    /**
     * 任务重试
     */
    Tuple<Boolean, String> retry(long taskVersionId);

    /**
     * 深度修复
     * @param taskVersionId
     * @return
     */
    boolean deepFix(long taskVersionId);

    /**
     * 置为成功
     * @param taskVersionId
     * @return
     */
    boolean ignore(long taskVersionId);

    /**
     * 添加任务版本的实例
     * @param taskVersionId
     * @param pluginId
     * @param pluginConfig
     * @param logicRunTime
     * @return
     */
    TaskInstance addVersionInstance(long taskVersionId, long pluginId, String pluginConfig, Date logicRunTime);

    /**
     * 创建实例对上游任务版本的依赖
     * @param instance
     */
    void addInstanceDependencies(long instance);

    /**
     * 获取可用的任务版本，即finalStatus为undefined
     * @param taskId
     * @return
     */
    List<TaskVersion> getValidVersion(long taskId);

    /**
     * 获取id
     * @param versionIds
     * @return
     */
    List<TaskVersion> getByIds(List<Long> versionIds);

    /**
     * 获取下游版本
     * @param taskVersionId
     * @return
     */
    List<Long> getDownstreamVersionIds(long taskVersionId);

    /**
     * 计算任务版本、实例以及实例依赖
     * @param taskId
     * @param startTime
     * @param endTime
     */
    void calVersionAndInstanceWithDependency(long taskId, Date startTime, Date endTime);

    /**
     * 获取小于某个版本的数量
     * @param taskId
     * @param finalStatus
     * @param versionNo
     * @return
     */
    int getLessThanNoCount(long taskId, int finalStatus, long versionNo);
}
