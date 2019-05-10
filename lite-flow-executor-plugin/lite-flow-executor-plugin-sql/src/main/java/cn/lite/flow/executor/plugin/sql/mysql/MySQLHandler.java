package cn.lite.flow.executor.plugin.sql.mysql;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.executor.plugin.sql.base.SQLHandler;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @description: MYSQL
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class MySQLHandler implements SQLHandler {

    private final static Logger LOG = LoggerFactory.getLogger(MySQLHandler.class);

    private final static String DB_URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";

    @Override
    public boolean handleSQL(JSONObject configMap) throws Throwable {

        String sql = configMap.getString(CommonConstants.PARAM_SQL);
        String url = configMap.getString(CommonConstants.PARAM_IP);
        String user = configMap.getString(CommonConstants.PARAM_USER);
        String pwd = configMap.getString(CommonConstants.PARAM_PASSWORD);
        String port = configMap.getString(CommonConstants.PARAM_PORT);
        String db = configMap.getString(CommonConstants.PARAM_DB);

        if(StringUtils.isBlank(sql)){
            LOG.error("sql is empty");
            return false;
        }

        String dbUrl = String.format(DB_URL_TEMPLATE, url, port, db);
        LOG.info("mysql db url is {}", dbUrl);

        Connection connection = null;
        Statement statement = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, user, pwd);
            connection.setAutoCommit(false);
            /**
             * 可以同时执行多条sql，以';'分割
             */
            String[] sqlItems = StringUtils.split(sql, CommonConstants.SEMICOLON);
            for(String sqlItem : sqlItems){
                if(StringUtils.isBlank(sqlItem)){
                    continue;
                }
                LOG.info("mysql db execute sql {}", sqlItem);
                statement = connection.createStatement();
                statement.executeUpdate(sqlItem);
            }
            return true;
        }catch (Throwable e){
            connection.rollback();
            LOG.error("execute sql error,connection rollback", e);
            throw new RuntimeException(e);

        }finally {
            if(connection != null){
                connection.commit();
                if(statement != null){
                    statement.close();
                }
                connection.close();
            }
        }
    }
}
