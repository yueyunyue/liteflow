package cn.lite.flow.console.web.controller.common;

import cn.lite.flow.common.conf.HadoopConfig;
import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.ExceptionUtils;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.executor.client.ExecutorAttachmentRpcService;
import cn.lite.flow.executor.model.basic.ExecutorAttachment;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by yueyunyue on 2019/05/06.
 */
@RestController("consoleHdfsCommonController")
@RequestMapping("executor/common/attachment")
@AuthCheckIgnore
public class AttachmentCommonController extends BaseController {

    private final static Logger LOG = LoggerFactory.getLogger(AttachmentCommonController.class);

    @Autowired
    private ExecutorAttachmentRpcService attachmentRpcService;
    /**
     * 上传
     * @param request
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String upload(HttpServletRequest request){

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        if(CollectionUtils.isEmpty(files)){
            return ResponseUtils.error("file is empty");
        }
        HadoopConfig hadoopConf = HadoopConfig.getHadoopConf();

        List<String> uploadHdfsFiles = files.stream().map(file -> {
            File localFile = null;
            try {
                String fileName = System.currentTimeMillis() + CommonConstants.LINE + file.getOriginalFilename();
                localFile = new File(hadoopConf.getLocalTmpDirPath() + CommonConstants.FILE_SPLIT + fileName);
                /**
                 * 先保存本地
                 */
                file.transferTo(localFile);

                ExecutorAttachment attachment = new ExecutorAttachment();
                attachment.setName(file.getOriginalFilename());
                attachment.setContent(FileUtils.readFileToString(localFile, CommonConstants.UTF8));
                String url = attachmentRpcService.add(attachment);
                return url;
            } catch (Throwable e) {
                LOG.error("uploadLocalFile2Hdfs hdfs file", e);
                throw new ConsoleRuntimeException(e.getMessage());
            } finally {
                if(localFile != null){
                    localFile.delete();
                }
            }
        }).collect(Collectors.toList());
        return ResponseUtils.success(uploadHdfsFiles);
    }
    /**
     * 下载
     * @param attachmentUrl
     * @param response
     */
    @RequestMapping(value = "download")
    public void download(@RequestParam("url")String attachmentUrl, HttpServletResponse response){
        OutputStream outputStream = null;
        try {
            String fileName = StringUtils.substringAfterLast(attachmentUrl, CommonConstants.FILE_SPLIT);
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String(fileName.getBytes("utf-8")));
            ExecutorAttachment attachment = attachmentRpcService.getByUrl(attachmentUrl);

            outputStream = response.getOutputStream();
            outputStream.write(attachment.getContent().getBytes(CommonConstants.UTF8_CHARSET));

        } catch (Throwable e) {
            LOG.error("download file error:{}", attachmentUrl, e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }
    /**
     * 获取文件内容
     * @param attachmentUrl
     */
    @RequestMapping(value = "getFileContent")
    public String getFileContent(@RequestParam("url")String attachmentUrl){
        try {
            ExecutorAttachment attachment = attachmentRpcService.getByUrl(attachmentUrl);
            return ResponseUtils.success(attachment.getContent());
        } catch (Throwable e) {
            LOG.error("download file error:{}", attachmentUrl, e);
            return ResponseUtils.error(ExceptionUtils.collectStackMsg(e));
        }
    }


}
