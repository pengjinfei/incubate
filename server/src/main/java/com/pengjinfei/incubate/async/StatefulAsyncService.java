package com.pengjinfei.incubate.async;

import com.pengjinfei.incubate.constants.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author PENGJINFEI533
 * @since 2018-04-18
 */
@Service
public class StatefulAsyncService {

	private final RedisTemplate<String, Object> redisTemplate;

	private static RedisTemplate<String, Object> template;

	@Autowired
	public StatefulAsyncService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public static void async(AsyncPayload payload) {
		template.opsForZSet().add(RedisConstant.ZSET_KEY, payload, 1.1);
	}

	@PostConstruct
	public void init() {
		template = this.redisTemplate;
	}

}
