package cn.lite.flow.executor.plugin.sql;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.SQLType;
import cn.lite.flow.executor.plugin.sql.base.SQLHandler;
import cn.lite.flow.executor.plugin.sql.hive.HiveSQLHandler;
import cn.lite.flow.executor.plugin.sql.mysql.MySQLHandler;
import cn.lite.flow.executor.plugin.sql.spark.SparkSQLHandler;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @description: 入口方法
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class SQLMain {

    private final static Logger LOG = LoggerFactory.getLogger(SQLMain.class);

    public static void main(String[] args) throws Throwable {
        LOG.info("args is {}", args);
        String arg0 = args[0];
        String data = FileUtils.readFileToString(new File(arg0), CommonConstants.UTF8_CHARSET);
        LOG.info("sql config is {}" + data);
        JSONObject dataObj = JSONObject.parseObject(data);

        Integer type = dataObj.getInteger(CommonConstants.PARAM_TYPE);

        SQLType sqlType = SQLType.getType(type);
        boolean success = false;
        SQLHandler sqlHandler = null;
        switch (sqlType){
            case MYSQL:
                sqlHandler = new MySQLHandler();
                break;
            case HIVE_SQL:
                sqlHandler = new HiveSQLHandler();
                break;
            case SPARK_SQL:
                sqlHandler = new SparkSQLHandler();
                break;

        }
        if(sqlHandler == null){
            LOG.error("there is no sql handler for type:{}" ,sqlType);
            System.exit(CommonConstants.SYSTEM_EXIT_ERROR);

        }
        success = sqlHandler.handleSQL(dataObj);
        /**
         * 1.执行成功正常退出
         * 2.失败异常退出
         */
        if(success){
            System.exit(CommonConstants.SYSTEM_EXIT_SUCCESS);
        }else {
            System.exit(CommonConstants.SYSTEM_EXIT_ERROR);
        }
    }


}
