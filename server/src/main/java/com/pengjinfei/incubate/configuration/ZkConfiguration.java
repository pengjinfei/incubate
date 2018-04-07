package com.pengjinfei.incubate.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
@Configuration
public class ZkConfiguration {

    @Bean
    public CuratorFramework zkClient(@Value("${zk.server}") String zkServer,@Value("${zk.namespace}}") String namespace) {
        return CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(namespace)
                .build();
    }
}
