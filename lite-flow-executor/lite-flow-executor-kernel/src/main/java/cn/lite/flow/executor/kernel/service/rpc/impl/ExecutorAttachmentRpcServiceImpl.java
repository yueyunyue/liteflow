package cn.lite.flow.executor.kernel.service.rpc.impl;

import cn.lite.flow.common.model.consts.FileType;
import cn.lite.flow.common.model.query.Page;
import cn.lite.flow.executor.client.ExecutorAttachmentRpcService;
import cn.lite.flow.executor.client.model.AttachmentParam;
import cn.lite.flow.executor.model.basic.ExecutorAttachment;
import cn.lite.flow.executor.model.query.ExecutorAttachmentQM;
import cn.lite.flow.executor.service.ExecutorAttachmentService;
import cn.lite.flow.executor.service.utils.ExecutorFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @description: 附件相关
 * @author: yueyunyue
 * @create: 2019-05-08
 **/
@Service("executorAttachmentRpcServiceImpl")
public class ExecutorAttachmentRpcServiceImpl implements ExecutorAttachmentRpcService {

    private final static Logger LOG = LoggerFactory.getLogger(ExecutorAttachmentRpcServiceImpl.class);

    @Autowired
    private ExecutorAttachmentService attachmentService;

    @Override
    public String add(ExecutorAttachment attachment) {
        attachmentService.add(attachment);
        String url = ExecutorFileUtils.generateAttachmentToUrl(attachment);
        return url;
    }

    @Override
    public List<ExecutorAttachment> list(AttachmentParam attachmentParam) {
        return attachmentService.list(getExecutorAttachmentQM(attachmentParam));
    }


    private ExecutorAttachmentQM getExecutorAttachmentQM(AttachmentParam attachmentParam) {
        ExecutorAttachmentQM qm = new ExecutorAttachmentQM();
        qm.setId(attachmentParam.getId());
        qm.setStatus(attachmentParam.getStatus());
        qm.setPage(Page.getPageByPageNo(attachmentParam.getPageNum(), attachmentParam.getPageSize()));
        return qm;
    }
    @Override
    public int count(AttachmentParam attachmentParam) {
        return attachmentService.count(getExecutorAttachmentQM(attachmentParam));
    }

    @Override
    public ExecutorAttachment getById(long id) {
        return attachmentService.getById(id);
    }

    @Override
    public ExecutorAttachment getByUrl(String url) {
        return attachmentService.getByUrl(url);
    }

    @Override
    public String getFileContent(String url) throws IOException {
        return ExecutorFileUtils.getFileContent(url);
    }

}
