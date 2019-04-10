package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.AsyncContainer;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: spark on yarn
 * @author: yueyunyue
 * @create: 2019-04-10
 **/
public class SparkOnYarnContainer extends AsyncContainer {

    private final static Logger LOG = LoggerFactory.getLogger(SparkOnYarnContainer.class);

    private final SparkConf sparkConf;

    public SparkOnYarnContainer(ExecutorJob executorJob) {
        super(executorJob);
        sparkConf = new SparkConf(false);



    }

    @Override
    public void checkStatus() {

    }

    @Override
    public void run() throws Exception {

    }

    @Override
    public void kill() throws Exception {

    }

}
