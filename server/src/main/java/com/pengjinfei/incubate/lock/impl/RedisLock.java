package com.pengjinfei.incubate.lock.impl;

import com.pengjinfei.incubate.lock.Lock;
import com.pengjinfei.incubate.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author PENGJINFEI533
 * @since 2018-04-16
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLock implements Lock {

	private final String namespace;

	private ThreadLocal<String> randomVal = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

	@Setter
	private TimeUnit unit = TimeUnit.MINUTES;
	@Setter
	private long time = 1;

	private final RedisService redisService;

	@Override
	public void release(String path) {
		String key = getRealPath(path);
		String  value = randomVal.get();
		try {
			redisService.removeIfEquals(key, value);
		} catch (Exception e) {
			log.error(String.format("release lock %s failed.", key), e);
		}
	}

	@Override
	public boolean tryLock(String path) {
		String key = getRealPath(path);
		String  value = randomVal.get();
		try {
			return redisService.setIfNotExists(key, value, time, unit);
		} catch (Exception e) {
			return false;
		}
	}

	private String getRealPath(String origin) {
		return namespace + ":" + origin;
	}
}
