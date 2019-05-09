package cn.lite.flow.plugin.sql.mysql;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.SQLType;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public class MysqlTest {

    @Test
    public void generateParam(){

        JSONObject configObj = new JSONObject();

        configObj.put(CommonConstants.PARAM_TYPE, SQLType.MYSQL.getValue());
        configObj.put(CommonConstants.PARAM_SQL, "select version()");
        configObj.put(CommonConstants.PARAM_URL, "db.liteflow.cn");
        configObj.put(CommonConstants.PARAM_PORT, "3306");
        configObj.put(CommonConstants.PARAM_USER, "lite");
        configObj.put(CommonConstants.PARAM_PASSWORD, "lite");
        configObj.put(CommonConstants.PARAM_DB, "lite_flow");


        System.out.println(configObj.toJSONString());


    }

}
