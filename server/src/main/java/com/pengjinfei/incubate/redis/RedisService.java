package com.pengjinfei.incubate.redis;

import com.pengjinfei.incubate.constants.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author PENGJINFEI533
 * @since 2018-04-18
 */
@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private Map<String, RedisScript> redisScriptMap;

	@Autowired
	public RedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public boolean setIfNotExists(final String key, final Object value, final long time, final TimeUnit unit) {
		try {
			List<Object> execute = redisTemplate.execute(new SessionCallback<List<Object>>() {
				@Override
				public <K, V> List<Object> execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
					redisOperations.watch((K) key);
					V v = redisOperations.opsForValue().get(key);
					if (v != null) {
						redisOperations.unwatch();
						return new ArrayList<>();
					}
					redisOperations.multi();
					redisOperations.opsForValue().set((K) key, (V) (value));
					redisOperations.expire((K) key, time, unit);
					return redisOperations.exec();
				}
			});
			if (CollectionUtils.isEmpty(execute)) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean removeIfEquals(final String key, final Object value) {
		try {
			List<Object> execute = redisTemplate.execute(new SessionCallback<List<Object>>() {
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
			if (CollectionUtils.isEmpty(execute)) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public <T> List<T> pollMultiFromZset(String key, int size, Class<T> resClass) throws Exception {
		List<Object> objectList = redisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
				operations.multi();
				BoundZSetOperations<K, V> setOps = operations.boundZSetOps((K) key);
				setOps.range(0, size - 1);
				setOps.removeRange(0, size - 1);
				return operations.exec();
			}
		});
		List<T> res = null;
		if (!CollectionUtils.isEmpty(objectList)) {
			Object resoriginSet = objectList.get(0);
			if (resoriginSet instanceof Set) {
				Set originSet = (Set) resoriginSet;
				res = new ArrayList<>(originSet);
			}
			Long lines= (Long) objectList.get(1);
			if (res !=null && res.size() != lines) {
				throw new ConcurrencyFailureException(String.format("poll %s objects from zset[%s] failed.", key, size));
			}
		}
		return res;
	}

	public void transferZset2List(String zsetKey, String listKey, int num, int topLevel) {
		RedisScript<Long> script = redisScriptMap.get(RedisConstant.LUA_ZSET2LIST);
		redisTemplate.execute(script, Arrays.asList(zsetKey,listKey,String.valueOf(num),String.valueOf(topLevel)));
	}
}
