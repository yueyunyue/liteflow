package cn.lite.flow.console.web.controller.common;

import cn.lite.flow.common.conf.HadoopConfig;
import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.ExceptionUtils;
import cn.lite.flow.common.utils.HadoopUtils;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.controller.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("console/common/hdfs")
@AuthCheckIgnore
public class HdfsCommonController extends BaseController {

    private final static Logger LOG = LoggerFactory.getLogger(HdfsCommonController.class);
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
        String hdfsWorkSpace = hadoopConf.getHdfsWorkspace();

        List<String> uploadHdfsFiles = files.stream().map(file -> {
            File localFile = null;
            try {
                String fileName = System.currentTimeMillis() + CommonConstants.LINE + file.getOriginalFilename();
                localFile = new File(hadoopConf.getLocalTmpDirPath() + CommonConstants.FILE_SPLIT + fileName);
                /**
                 * 先保存本地
                 */
                file.transferTo(localFile);
                String hdfsFilePath = hdfsWorkSpace + CommonConstants.FILE_SPLIT + fileName;
                String hdfsPath = HadoopUtils.uploadLocalFile2Hdfs(localFile, hdfsFilePath);

                return hdfsPath;
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
     * @param hdfsFilePath
     * @param response
     */
    @RequestMapping(value = "download")
    public void download(@RequestParam("hdfsFilePath")String hdfsFilePath, HttpServletResponse response){
        OutputStream outputStream = null;
        FSDataInputStream fsDataInputStream = null;
        try {
            String fileName = StringUtils.substringAfterLast(hdfsFilePath, CommonConstants.FILE_SPLIT);
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String(fileName.getBytes("utf-8")));
            outputStream = response.getOutputStream();
            FileSystem fileSystem = HadoopUtils.getFileSystem();
            fsDataInputStream = fileSystem.open(new Path(hdfsFilePath));
            IOUtils.copy(fsDataInputStream, outputStream);
        } catch (Throwable e) {
            LOG.error("download file error:{}", hdfsFilePath, e);
        } finally {
            IOUtils.closeQuietly(fsDataInputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
    /**
     * 获取文件内容
     * @param hdfsFilePath
     */
    @RequestMapping(value = "getFileContent")
    public String getFileContent(@RequestParam("hdfsFilePath")String hdfsFilePath){
        try {
            HadoopConfig hadoopConf = HadoopConfig.getHadoopConf();
            /**
             * 非上传路径下的不能查看
             */
            if(!StringUtils.contains(hdfsFilePath, hadoopConf.getHdfsWorkspace())){
                return ResponseUtils.error("非"+ hadoopConf.getHdfsWorkspace() + "路径下的不能查看");
            }
            String fileContent = HadoopUtils.getFileContent(hdfsFilePath, true);
            return ResponseUtils.success(fileContent);
        } catch (Throwable e) {
            LOG.error("download file error:{}", hdfsFilePath, e);
            return ResponseUtils.error(ExceptionUtils.collectStackMsg(e));
        }
    }


}
