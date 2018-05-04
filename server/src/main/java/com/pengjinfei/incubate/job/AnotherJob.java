package com.pengjinfei.incubate.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author PENGJINFEI533
 * @since 2018-03-09
 */
@Slf4j
@Component
public class AnotherJob implements SimpleJob {


	@Override
	public void execute(ShardingContext shardingContext) {
		log.debug("runing another job");
	}
}
