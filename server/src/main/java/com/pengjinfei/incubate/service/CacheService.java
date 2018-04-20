package com.pengjinfei.incubate.service;

import com.pengjinfei.incubate.cache.config.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author PENGJINFEI533
 * @since 2018-04-20
 */
@Service
@Slf4j
public class CacheService {

	@Cacheable(key = "#root.args[0]",value = RedisConstants.HALF_HOUR)
	public String getName(Integer id) {
		log.info("get data from db.");
		return "test";
	}

	@Cacheable(key = "#root.args[0]",value = RedisConstants.ONE_HOUR)
	public String getNameByAnotherCache(Integer id) {
		log.info("get data from db.");
		return "oneHour";
	}
}
