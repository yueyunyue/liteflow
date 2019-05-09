package cn.lite.flow.executor.plugin.sql.hive;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.plugin.sql.base.SQLHandler;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @description: MYSQL
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class HiveSQLHandler implements SQLHandler {

    private final static Logger LOG = LoggerFactory.getLogger(HiveSQLHandler.class);

    private final static String DB_URL_TEMPLATE = "jdbc:hive2://%s:%s/%s";
    private final static String SET_QUEUE = "set mapreduce.job.queuename=";

    @Override
    public boolean handleSQL(JSONObject configMap) throws Throwable {

        String sql = configMap.getString(CommonConstants.PARAM_SQL);
        String url = configMap.getString(CommonConstants.PARAM_URL);
        String user = configMap.getString(CommonConstants.PARAM_USER);
        String pwd = configMap.getString(CommonConstants.PARAM_PASSWORD);
        String port = configMap.getString(CommonConstants.PARAM_PORT);
        String db = configMap.getString(CommonConstants.PARAM_DB);

        String queue = configMap.getString(CommonConstants.PARAM_QUEUE);

        if (StringUtils.isBlank(sql)) {
            LOG.error("sql is empty");
            return false;
        }

        String dbUrl = String.format(DB_URL_TEMPLATE, url, port, db);
        LOG.info("mysql db url is {}", dbUrl);

        Connection connection = null;
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            connection = DriverManager.getConnection("jdbc:hive2://10.26.15.168:10000/default", "", "");
            LOG.info("hive sql execute sql {}", sql);
            String setQueueCommand = SET_QUEUE + queue;
            LOG.info("set queue to hive sql", setQueueCommand);

            connection.createStatement().executeUpdate(setQueueCommand);
            connection.createStatement().executeUpdate(sql);
            return true;
        } catch (Throwable e) {
            LOG.error("execute hive sql error", e);
            throw new RuntimeException(e);

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
