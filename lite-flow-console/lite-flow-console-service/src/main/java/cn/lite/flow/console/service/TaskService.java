package cn.lite.flow.console.service;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.query.TaskQM;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ly on 2018/7/23.
 */
public interface TaskService extends BaseService<Task, TaskQM> {

    List<Task> getByIds(List<Long> taskIds);

    /**
     * 批量获取任务信息并返回map
     * @param taskIds
     * @return
     */
    Map<Long, Task> getTaskInfo(List<Long> taskIds);

    /**
     * 上线
     * @param taskId
     * @return
     */
    boolean online(long taskId);

    /**
     * 下线
     * @param taskId
     * @return
     */
    boolean offline(long taskId);

    /**
     * 尝试下线,主要用在任务流一起下线时，避免部分下线，部分没有下线的情况；
     * 保证一致性
     * @param taskId
     * @return
     */
    Tuple<Boolean, List<TaskDependency>> tryOffline(long taskId);

    /**
     * 统计一定状态的任务数量
     *
     * @param status        任务状态     如果为null，则查询总数
     * @return
     */
    int statisByStatus(Integer status);

    /**
     * 运行当前任务
     * @param taskId
     */
    void run(long taskId);

}
