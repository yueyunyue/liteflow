package cn.lite.flow.executor.plugin.sql.hive;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.PropertyUtils;
import cn.lite.flow.executor.plugin.sql.base.SQLHandler;
import cn.lite.flow.executor.plugin.sql.model.consts.SQLConstants;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

/**
 * @description: MYSQL
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class HiveSQLHandler implements SQLHandler {

    private final static Logger LOG = LoggerFactory.getLogger(HiveSQLHandler.class);

    private final static String DB_URL_TEMPLATE = "jdbc:hive2://%s:%s/%s";

    private final boolean isDefault;

    public HiveSQLHandler(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public boolean handleSQL(JSONObject configMap) throws Throwable {

        String sql = configMap.getString(CommonConstants.PARAM_SQL);
        String queue = configMap.getString(CommonConstants.PARAM_QUEUE);

        String ip = null;
        String port = null;
        String db = null;
        String user = null;
        String passwd = null;

        if(isDefault){
//            String propertyPath = Thread.currentThread().getContextClassLoader().getResource().toURI().toString();
            Properties property = PropertyUtils.getProperty("/Users/yueyunyue/workspace4m/liteflow/lite-flow-executor-plugin/lite-flow-executor-plugin-sql/src/main/resources/dev/config.properties");
            ip = property.getProperty(SQLConstants.HIVE_IP);
            port = property.getProperty(SQLConstants.HIVE_PORT);
            db = property.getProperty(SQLConstants.HIVE_DB);
            user = property.getProperty(SQLConstants.HIVE_USER);
            passwd = property.getProperty(SQLConstants.HIVE_PASSWD);
        }else {
            ip = configMap.getString(CommonConstants.PARAM_IP);
            user = configMap.getString(CommonConstants.PARAM_USER);
            passwd = configMap.getString(CommonConstants.PARAM_PASSWORD);
            port = configMap.getString(CommonConstants.PARAM_PORT);
            db = configMap.getString(CommonConstants.PARAM_DB);
        }

        LOG.info("hive config ip:{} port:{} db:{} user:{}", ip, port, db, user);
        if (StringUtils.isBlank(sql)) {
            LOG.error("sql is empty");
            return false;
        }

        String dbUrl = String.format(DB_URL_TEMPLATE, ip, port, db);
        LOG.info("hive db url is {}", dbUrl);
        Connection connection = null;
        HiveStatement hiveStatement;
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            connection = DriverManager.getConnection(dbUrl, user, passwd);

            String setQueueCommand = "set mapreduce.job.queuename=" + queue;
            LOG.info("set queue to hive sql : {}", setQueueCommand);

            String setDynamicCommand = "set hive.exec.dynamic.partition=true";
            LOG.info("set hive.exec.dynamic.partition : {}", setDynamicCommand);

            String setDynamicNonstrictCommand = "set hive.exec.dynamic.partition.mode=nonstrict";
            LOG.info("set hive.exec.dynamic.partition.mode : {}", setDynamicNonstrictCommand);

            connection.createStatement().executeUpdate(setQueueCommand);
            connection.createStatement().executeUpdate(setDynamicCommand);
            connection.createStatement().executeUpdate(setDynamicNonstrictCommand);

            Statement sqlStatement = connection.createStatement();
            hiveStatement = (HiveStatement) sqlStatement;

            HiveLogCollector sqlCollector = new HiveLogCollector(hiveStatement);
            sqlCollector.start();
            LOG.info("execute hive sql :{}", sql);
            sqlStatement.executeUpdate(sql);
            sqlCollector.join();
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
