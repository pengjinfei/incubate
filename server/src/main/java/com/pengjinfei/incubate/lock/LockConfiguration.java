package com.pengjinfei.incubate.lock;

import com.pengjinfei.incubate.lock.impl.ZkLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class LockConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LockAdvisor lockAdvisor(Lock zkLock) {
        return new LockAdvisor(zkLock);
    }

    @Bean
    @ConditionalOnMissingBean(Lock.class)
    public Lock zkLock(@Value("${zk.server}") String zkServer, @Value("${zk.lock.namespace}") String namespace){
        CuratorFramework framework = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(namespace)
                .build();
        framework.start();
        return new ZkLock(framework);
    }
}
