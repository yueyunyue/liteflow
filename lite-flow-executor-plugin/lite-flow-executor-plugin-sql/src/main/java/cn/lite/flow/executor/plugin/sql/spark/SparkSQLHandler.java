package cn.lite.flow.executor.plugin.sql.spark;

import cn.lite.flow.executor.plugin.sql.base.SQLHandler;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description: MYSQL
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class SparkSQLHandler implements SQLHandler {

    private final static Logger LOG = LoggerFactory.getLogger(SparkSQLHandler.class);

    @Override
    public boolean handleSQL(JSONObject configMap) throws Throwable {

        return false;
    }
}
