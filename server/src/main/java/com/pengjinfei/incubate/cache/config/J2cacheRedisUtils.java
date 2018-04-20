package com.pengjinfei.incubate.cache.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static com.pengjinfei.incubate.cache.config.RedisConstants.REDIS_TEMPLATE_BEAN_NAME;

/**
 * @author PENGJINFEI533
 * @since 2018-03-07
 */
public class J2cacheRedisUtils implements InitializingBean {

	@Autowired
	@Qualifier(REDIS_TEMPLATE_BEAN_NAME)
	RedisTemplate<String, Object> localRedisTemplate;

	private static RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RedisConnectionFactory localFactory;

	private static RedisConnectionFactory factory;

	public static RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public static RedisConnectionFactory getRedisConnectionFactory() {
		return factory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		redisTemplate = this.localRedisTemplate;
		factory=this.localFactory;
	}
}
