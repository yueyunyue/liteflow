package cn.lite.flow.console.kernel.runner;

import cn.lite.flow.console.common.election.MasterInfo;
import cn.lite.flow.common.utils.IpUtils;
import cn.lite.flow.console.service.queue.EventQueue;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by luya on 2019/1/4.
 */
public class StartupRunner implements CommandLineRunner {

    private final static Logger LOG = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private SchedulerFactoryBean scheduler;

    @Autowired
    private CuratorFramework client;

    @Value("${zk.console.leader.path}")
    private String path;

    @Override
    public void run(String... strings) throws Exception {
        LeaderLatch leaderLatch = new LeaderLatch(client, path);
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                MasterInfo.setIsMaster(true);
                refreshLeaderIp();
                scheduler.start();
            }

            @Override
            public void notLeader() {
                MasterInfo.setIsMaster(false);
                scheduler.stop();
                EventQueue.clear();
            }
        });
        leaderLatch.start();
    }

    private void refreshLeaderIp() {
        String localIp = IpUtils.getIp();
        try {
            client.setData().forPath(path, localIp.getBytes());
        } catch (Throwable e) {
            LOG.error("refresh leader ip:{} to zk error!", localIp);
        }
    }
}
