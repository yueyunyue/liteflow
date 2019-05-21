package cn.lite.flow.executor.plugin.sql.hive;

import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: hive日志搜集
 * @author: yueyunyue
 * @create: 2019-05-10
 **/
public class HiveLogCollector extends Thread {

    private final static Logger LOG = LoggerFactory.getLogger(HiveLogCollector.class);

    private HiveStatement hiveStatement;

    private long sleepTime = 500L;

    public HiveLogCollector(HiveStatement hiveStatement) {
        this.hiveStatement = hiveStatement;
    }

    public HiveLogCollector(HiveStatement hiveStatement, long sleepTime) {
        this.hiveStatement = hiveStatement;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {

        if (hiveStatement == null) {
            return;
        }
        try {
            while (!hiveStatement.isClosed() && hiveStatement.hasMoreLogs()) {
                List<String> logs = hiveStatement.getQueryLog();
                for (String log : logs) {
                    LOG.info(log);
                }
                Thread.sleep(sleepTime);
            }
        } catch (Throwable e) {
            LOG.error("hive log collector error", e);
        }
    }
}
