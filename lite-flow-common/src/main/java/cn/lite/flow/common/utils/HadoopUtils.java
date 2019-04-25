package cn.lite.flow.common.utils;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
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
import java.util.List;

/**
 * @description: hadoop相关工具类
 * @author: yueyunyue
 * @create: 2019-04-23
 **/
public class HadoopUtils {

    public static final Logger LOG = LoggerFactory.getLogger(HadoopUtils.class);

    private static volatile FileSystem FILE_SYSTEM = null;

    private final static int COPY_BUFF_BYTE_SIZE = 4096;

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
    public static String upload(File fileItem, String fsFileName) throws IOException {
        String location;
        InputStream inputStream = null;
        FSDataOutputStream os = null;
        try {
            inputStream = new FileInputStream(fileItem);
            Path dstPath = new Path(fsFileName);
            FileSystem fileSystem = getFileSystem();
            os = fileSystem.create(dstPath);
            IOUtils.copyBytes(inputStream, os, COPY_BUFF_BYTE_SIZE, false);
            location = fileSystem.getFileStatus(dstPath).getPath().toUri().toString();
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(os);
            org.apache.commons.io.IOUtils.closeQuietly(inputStream);
        }
        return location;
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

}
