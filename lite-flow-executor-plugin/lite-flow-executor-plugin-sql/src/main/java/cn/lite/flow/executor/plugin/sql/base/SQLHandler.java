package cn.lite.flow.executor.plugin.sql.base;

import com.alibaba.fastjson.JSONObject;

/**
 * @description: 处理sql
 * @author: yueyunyue
 * @create: 2019-05-09
 **/
public interface SQLHandler {

    /**
     * 处理
     * @param configMap
     * @return
     */
    boolean handleSQL(JSONObject configMap) throws Throwable;

}
