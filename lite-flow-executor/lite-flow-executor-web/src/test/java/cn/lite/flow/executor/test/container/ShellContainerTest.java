package cn.lite.flow.executor.test.container;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.kernel.container.ContainerFactory;
import cn.lite.flow.executor.kernel.container.impl.DirectShellContainer;
import cn.lite.flow.executor.kernel.container.impl.ShellContainer;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import cn.lite.flow.executor.model.kernel.Container;
import cn.lite.flow.executor.service.ExecutorContainerService;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.test.base.BaseTest;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 容器测试
 * @author: yueyunyue
 * @create: 2019-01-27
 **/
public class ShellContainerTest extends BaseTest {

    @Autowired
    private ExecutorContainerService containerService;

    @Autowired
    private ExecutorJobService jobService;


    @Test
    public void testShell() throws Exception {

        JSONObject param = new JSONObject();
        param.put(Constants.SHELL_CONTENT, "touch /tmp/123.log");
        param.put(CommonConstants.PARAM_EXECUTOR_JOB_NAME, "shell");

        ExecutorJob job = new ExecutorJob();
        job.setId(1l);
        job.setConfig(param.toJSONString());
        job.setStatus(ExecutorJobStatus.NEW.getValue());

        DirectShellContainer shellContainer = new DirectShellContainer(job);
        shellContainer.run();



    }

}
