package com.pengjinfei.incubate.lock.impl;

import com.pengjinfei.incubate.lock.Lock;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	private final RedisTemplate<String,Object> template;

	private ThreadLocal<String> randomVal = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

	@Setter
	private TimeUnit unit = TimeUnit.MINUTES;
	@Setter
	private long time = 1;

	@Override
	public void release(String path) {
		String key = getRealPath(path);
		String  value = randomVal.get();
		try {
			template.execute(new SessionCallback<List<Object>>() {
				@Override
				public <K, V> List<Object> execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
					redisOperations.watch((K) key);
					V v = redisOperations.opsForValue().get(key);
					if (v == null || !v.equals(value)) {
						redisOperations.unwatch();
						return new ArrayList<>();
					}
					redisOperations.multi();
					redisOperations.delete((K) key);
					return redisOperations.exec();
				}
			});
		} catch (Exception e) {
			log.error(String.format("release lock %s failed.", key), e);
		}
	}

	@Override
	public boolean tryLock(String path) {
		String key = getRealPath(path);
		String  value = randomVal.get();
		try {
			List<Object> execute = template.execute(new SessionCallback<List<Object>>() {
				@Override
				public <K, V> List<Object> execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
					redisOperations.watch((K) key);
					V v = redisOperations.opsForValue().get(key);
					if (v != null) {
						redisOperations.unwatch();
						return Collections.singletonList(true);
					}
					redisOperations.multi();
					redisOperations.opsForValue().set((K) key, (V) (value),time,unit);
					return redisOperations.exec();
				}
			});
			if (!CollectionUtils.isEmpty(execute)) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private String getRealPath(String origin) {
		return namespace + ":" + origin;
	}
}
