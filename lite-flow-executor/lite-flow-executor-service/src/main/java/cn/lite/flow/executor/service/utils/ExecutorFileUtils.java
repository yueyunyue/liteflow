package cn.lite.flow.executor.service.utils;


import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.FileType;
import cn.lite.flow.common.utils.HadoopUtils;
import cn.lite.flow.executor.model.basic.ExecutorAttachment;
import cn.lite.flow.executor.service.ExecutorAttachmentService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @description: 文件相关工具
 * @author: yueyunyue
 * @create: 2019-05-08
 **/
public class ExecutorFileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutorFileUtils.class);

    /**
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String getFileContent(String path) throws IOException {
        FileType fileType = FileType.getTypeByFileUrl(path);
        String content = null;
        switch (fileType){

            case LOCAL:
                content = FileUtils.readFileToString(new File(path), Charset.forName(CommonConstants.UTF8));
                break;

            case HDFS:
                content = HadoopUtils.getFileContent(path, true);
                break;

            case LITE_ATTACHMENT:
                ExecutorAttachmentService attachmentService = ExecutorServiceUtils.getExecutorAttachmentService();
                ExecutorAttachment attachment = attachmentService.getByUrl(path);
                content = attachment.getContent();
                break;
        }
        LOG.info("path:{} content is {}", path, content);
        return content;
    }

    /**
     * 生成attachment url
     * @param attachment
     * @return
     */
    public static String generateAttachmentToUrl(ExecutorAttachment attachment){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(CommonConstants.ATTACHMENT_PREFIX);
        urlBuilder.append(CommonConstants.EXECUTOR);
        urlBuilder.append(CommonConstants.URL_QUESTION);
        urlBuilder.append(CommonConstants.PARAM_ID).append(CommonConstants.EQUAL).append(attachment.getId());
        urlBuilder.append(CommonConstants.URL_PARAM_SPLIT);
        urlBuilder.append(CommonConstants.PARAM_NAME).append(CommonConstants.EQUAL).append(attachment.getName());

        return urlBuilder.toString();
    }

}
