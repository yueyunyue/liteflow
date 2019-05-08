package cn.lite.flow.executor.client;

import cn.lite.flow.executor.client.model.AttachmentParam;
import cn.lite.flow.executor.model.basic.ExecutorAttachment;

import java.util.List;

/**
 *
 */
public interface ExecutorAttachmentRpcService {

    /**
     * 添加
     * @param executorPlugin
     * @return  返回id
     */
    String add(ExecutorAttachment executorPlugin);

    /**
     * 获取列表
     * @return  返回10条
     */
    List<ExecutorAttachment> list(AttachmentParam attachmentParam);

    /**
     * 获取数量
     * @param attachmentParam
     * @return
     */
    int count(AttachmentParam attachmentParam);

    /**
     * 获取插件
     * @param id
     * @return
     */
    ExecutorAttachment getById(long id);

    /**
     * 通过生成的url获取
     * @param url
     * @return
     */
    ExecutorAttachment getByUrl(String url);


}
