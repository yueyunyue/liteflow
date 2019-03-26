package cn.lite.flow.console.kernel.conf;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ly on 2019/1/3.
 */
@Configuration
public class ZKClientConfig {

    @Value("${zk.servers}")
    private String servers;
    @Value("${zk.namespace}")
    private String namespace;

    @Bean("curatorZkClient")
    public CuratorFramework zkClient() {
        CuratorFramework zkClient = CuratorFrameworkFactory
                .builder()
                .connectString(servers)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .canBeReadOnly(false)
                .retryPolicy(new ExponentialBackoffRetry(2000, Integer.MAX_VALUE))
                .namespace(namespace)
                .defaultData(null)
                .build();
        zkClient.start();
        return zkClient;
    }
}
