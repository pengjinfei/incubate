package com.pengjinfei.incubate.job;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.pengjinfei.incubate.job.config.ElasticjobConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author PENGJINFEI533
 * @since 2018-03-09
 */
@Configuration
@ConditionalOnBean(ElasticjobConfiguration.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElasticJobs {

	private final ZookeeperRegistryCenter registryCenter;

	private final TestJob testJob;

	private final AnotherJob anotherJob;

	@Bean(initMethod = "init")
	public JobScheduler testJobScheduler() {
		LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
				JobCoreConfiguration.newBuilder("testJob", "*/9 * * * * ? ", 5).build(),
				TestJob.class.getCanonicalName())).overwrite(true).build();
		return new SpringJobScheduler(testJob, registryCenter, jobConfig);
	}

	@Bean(initMethod = "init")
	public JobScheduler anotherJobScheduler() {
		LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
				JobCoreConfiguration.newBuilder("anotherJob", "0 */1 * * * ? ", 5).build(),
				TestJob.class.getCanonicalName())).overwrite(true).build();
		return new SpringJobScheduler(anotherJob, registryCenter, jobConfig);
	}
}
