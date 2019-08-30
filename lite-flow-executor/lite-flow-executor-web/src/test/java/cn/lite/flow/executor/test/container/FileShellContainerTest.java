package cn.lite.flow.executor.test.container;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.kernel.container.impl.ShellContainer;
import cn.lite.flow.executor.model.basic.ExecutorAttachment;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.consts.ExecutorJobStatus;
import cn.lite.flow.executor.service.ExecutorContainerService;
import cn.lite.flow.executor.service.ExecutorJobService;
import cn.lite.flow.executor.service.utils.ExecutorFileUtils;
import cn.lite.flow.executor.test.base.BaseTest;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 容器测试
 * @author: yueyunyue
 * @create: 2019-01-27
 **/
public class FileShellContainerTest extends BaseTest {

    @Autowired
    private ExecutorContainerService containerService;

    @Autowired
    private ExecutorJobService jobService;


    @Test
    public void testShell() throws Exception {

        ExecutorAttachment attachment = new ExecutorAttachment();
        attachment.setId(1L);
        attachment.setName("123");
        String attachmentUrl = ExecutorFileUtils.generateAttachmentToUrl(attachment);

        JSONObject param = new JSONObject();
        param.put(CommonConstants.PARAM_FILE, attachmentUrl);
        param.put(CommonConstants.PARAM, "shell=123");

        ExecutorJob job = new ExecutorJob();
        job.setId(1l);
        job.setConfig(param.toJSONString());
        job.setStatus(ExecutorJobStatus.NEW.getValue());

        ShellContainer shellContainer = new ShellContainer(job);
        shellContainer.run();



    }

}
