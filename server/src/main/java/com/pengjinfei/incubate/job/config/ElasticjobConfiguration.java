package com.pengjinfei.incubate.job.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author PENGJINFEI533
 * @since 2018-03-08
 */
@Configuration
public class ElasticjobConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "zk.elastic-job")
	public ZookeeperConfiguration zookeeperConfiguration(@Value("${zk.server}") String serverLists,
														 @Value("${zk.elastic-job.namespace}") String namespace) {
		return new ZookeeperConfiguration(serverLists, namespace);
	}

	@Bean(initMethod = "init")
	public ZookeeperRegistryCenter registryCenter(ZookeeperConfiguration zookeeperConfiguration) {
		return new ZookeeperRegistryCenter(zookeeperConfiguration);
	}

}
