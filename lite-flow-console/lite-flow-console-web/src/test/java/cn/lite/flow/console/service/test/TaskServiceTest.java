package cn.lite.flow.console.service.test;

import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.service.TaskService;
import cn.lite.flow.console.test.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ly on 2018/7/23.
 */
public class TaskServiceTest extends BaseTest {

    @Autowired
    private TaskService taskService;

    @Test
    public void addTest() {
        Task task = new Task();
        task.setName("test");
        task.setCronExpression("0 0 12 * * ?");
        task.setPeriod(4);
        task.setVersion(0);
        task.setPluginId(0L);
        task.setPluginConf("");
        task.setUserId(0L);
        task.setIsRetry(0);
        task.setDescription("test");
        taskService.add(task);
    }

    @Test
    public void getByIdTest() {
        Task task = taskService.getById(1L);
        if (task != null) {


        }
    }

}
