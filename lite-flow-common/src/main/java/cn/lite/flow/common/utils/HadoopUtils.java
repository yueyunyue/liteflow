package cn.lite.flow.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * @description: hadoop相关工具类
 * @author: yueyunyue
 * @create: 2019-04-23
 **/
public class HadoopUtils {

    public static final Logger LOG = LoggerFactory.getLogger(HadoopUtils.class);

    private static volatile FileSystem FILE_SYSTEM = null;

    public final static int COPY_BUFF_BYTE_SIZE = 4096;

    /**
     * 获取fs
     *
     * @return
     */
    public static FileSystem getFileSystem() {
        if (FILE_SYSTEM == null) {
            synchronized (HadoopUtils.class) {
                try {
                    Configuration configuration = new Configuration();
                    FILE_SYSTEM = FileSystem.get(configuration);
                } catch (Throwable e) {
                    LOG.error("FILE_SYSTEM init error", e);
                }
            }
        }
        return FILE_SYSTEM;
    }

    /**
     * 上传至hdfs
     *
     * @param fileItem
     * @return
     * @throws IOException
     */
    public static String uploadLocalFile2Hdfs(File fileItem, String fsFileName) throws IOException {
        FileSystem fileSystem = getFileSystem();
        InputStream inputStream = null;
        FSDataOutputStream os = null;
        Path dstPath = new Path(fsFileName);
        try {
            inputStream = new FileInputStream(fileItem);
            os = fileSystem.create(dstPath);
            IOUtils.copyBytes(inputStream, os, COPY_BUFF_BYTE_SIZE);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(os);
            org.apache.commons.io.IOUtils.closeQuietly(inputStream);
        }
        return fileSystem.getFileStatus(dstPath).getPath().toUri().toString();
    }

    /**
     * 获取文件或文件夹文件的绝对路径
     * @param dir
     * @return
     */
    public static List<String> listFileOrDirFileNames(String dir) {

        FileSystem fileSystem = getFileSystem();
        Path path = new Path(dir);
        try {
            FileStatus[] fileStatuses = fileSystem.listStatus(path);
            if (fileStatuses != null) {
                List<String> files = Lists.newArrayList();
                for (FileStatus status : fileStatuses) {
                    files.add(status.getPath().toUri().toString());
                }
                return files;
            }
        } catch (Throwable e) {
            LOG.error("get dir:{} file list error", dir, e);
        }
        return null;
    }

    /**
     * 上传至hdfs
     *
     * @return
     * @throws IOException
     */
    public static String download(String fsFilePath, String localFilePath) throws IOException {
        FileSystem fileSystem = getFileSystem();
        fileSystem.copyToLocalFile(new Path(fsFilePath), new Path(localFilePath));
        return localFilePath;
    }

    /**
     * 获取文本内容
     * @param hdfsFilePath
     * @return
     */
    public static String getFileContent(String hdfsFilePath, boolean checkTxt){
        FSDataInputStream fsDataInputStream = null;
        try {
            if(checkTxt){
                boolean isPass = false;
                for(String suffix : CommonConstants.TEXT_FILE_SUFFIX){
                    if(hdfsFilePath.endsWith(suffix)){
                        isPass = true;
                    }
                }
                if(!isPass){
                    throw new RuntimeException("file is not txt type:" + hdfsFilePath);
                }
            }
            FileSystem fileSystem = HadoopUtils.getFileSystem();
            fsDataInputStream = fileSystem.open(new Path(hdfsFilePath));
            StringWriter stringWriter = new StringWriter();
            org.apache.commons.io.IOUtils.copy(fsDataInputStream, stringWriter);
            return stringWriter.toString();
        } catch (Throwable e) {
            LOG.error("download file error:{}", hdfsFilePath, e);
            throw new RuntimeException(e);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(fsDataInputStream);
        }

    }


}
