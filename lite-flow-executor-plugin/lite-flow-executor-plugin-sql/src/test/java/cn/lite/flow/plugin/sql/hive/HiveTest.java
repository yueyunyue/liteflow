package cn.lite.flow.plugin.sql.hive;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.SQLType;
import cn.lite.flow.executor.plugin.sql.hive.HiveSQLHandler;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class HiveTest {

    @Test
    public void testSql() throws Throwable{

        String insertSql = "insert into lite.test(id) values(66)";
        String selectSql = " select * from lite.test where  id > 100 group by id ";

        JSONObject configObj = new JSONObject();
        configObj.put(CommonConstants.PARAM_QUEUE, "default");
        configObj.put(CommonConstants.PARAM_IP, "10.26.15.168");
        configObj.put(CommonConstants.PARAM_PORT, "10000");
        configObj.put(CommonConstants.PARAM_USER, "hadoop");
        configObj.put(CommonConstants.PARAM_DB, "default");
        configObj.put(CommonConstants.PARAM_SQL, selectSql);

        HiveSQLHandler sqlHandler = new HiveSQLHandler();
        sqlHandler.handleSQL(configObj);

    }

}
