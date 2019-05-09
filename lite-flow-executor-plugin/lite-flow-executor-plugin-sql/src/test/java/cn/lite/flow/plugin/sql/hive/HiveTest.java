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

        JSONObject configObj = new JSONObject();

        configObj.put(CommonConstants.PARAM_SQL, "select 123");
        HiveSQLHandler sqlHandler = new HiveSQLHandler();
        sqlHandler.handleSQL(configObj);



    }

}
