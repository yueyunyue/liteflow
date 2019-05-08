package cn.lite.flow.executor.test.hdfs;

import cn.lite.flow.common.utils.HadoopUtils;
import org.junit.Test;

import java.util.List;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-04-24
 **/
public class HdfsTest {

    @Test
    public void testListFile(){
        List<String> files = HadoopUtils.listFileOrDirFileNames("/user/lite/spark2");
        System.out.println(files);
    }

}
