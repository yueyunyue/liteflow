package cn.lite.flow.common.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @description: hadoop相关工具类
 * @author: cyp
 * @create: 2019-04-23
 **/
public class HadoopUtils {

    public final Logger LOG = LoggerFactory.getLogger(HadoopUtils.class);

    private static FileSystem FILE_SYSTEM = null;

    static {
        try {
            Configuration configuration = new Configuration();
            FILE_SYSTEM = FileSystem.get(configuration);
        } catch (Throwable e) {


        }
    }
    /**
     * 上传至hdfs
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
            os = FILE_SYSTEM.create(dstPath);
            IOUtils.copyBytes(inputStream, os, 4096, false);
            location = FILE_SYSTEM.getFileStatus(dstPath).getPath().toUri().toString();
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(os);
            org.apache.commons.io.IOUtils.closeQuietly(inputStream);
        }
        return location;
    }

}
