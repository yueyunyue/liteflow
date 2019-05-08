package cn.lite.flow.executor.service;

import cn.lite.flow.common.service.basic.BaseService;
import cn.lite.flow.executor.model.basic.ExecutorAttachment;
import cn.lite.flow.executor.model.query.ExecutorAttachmentQM;

/**
 * @description: 执行插件
 * @author: yueyunyue
 * @create: 2018-09-13
 **/
public interface ExecutorAttachmentService extends BaseService<ExecutorAttachment, ExecutorAttachmentQM> {

    /**
     * 获取文件内容
     * @param url
     * @return
     */
    ExecutorAttachment getByUrl(String url);

}
