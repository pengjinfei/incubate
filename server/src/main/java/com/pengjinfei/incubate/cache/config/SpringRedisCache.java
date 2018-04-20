package com.pengjinfei.incubate.cache.config;

import net.oschina.j2cache.Level2Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author PENGJINFEI533
 * @since 2018-03-07
 */
@SuppressWarnings("unchecked")
public class SpringRedisCache implements Level2Cache {

	private String namespace;

	private String region;

	private RedisTemplate<String, Object> redisTemplate;

	public SpringRedisCache(String namespace, String region) {
		if (region == null || region.isEmpty()) {
			region = "_"; // 缺省region
		}
		this.namespace = namespace;
		this.redisTemplate = J2cacheRedisUtils.getRedisTemplate();
		this.region = getRegionName(region);
	}

	private String getRegionName(String region) {
		if (namespace != null && !namespace.isEmpty())
			region = namespace + ":" + region;
		return region;
	}

	private String getFullKey(String key) {
		return region + ":" + key;
	}

	@Override
	public boolean supportTTL() {
		return true;
	}

	@Override
	public byte[] getBytes(String key) {
		doWithBytes();
		return null;
	}

	@Override
	public List<byte[]> getBytes(Collection<String> keys) {
		doWithBytes();
		return null;
	}

	@Override
	public void setBytes(String key, byte[] bytes) {
		doWithBytes();
	}

	@Override
	public void setBytes(Map<String, byte[]> bytes) {
		doWithBytes();
	}

	@Override
	public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds) {
		doWithBytes();
	}

	@Override
	public void setBytes(Map<String, byte[]> bytes, long timeToLiveInSeconds) {
		doWithBytes();
	}

	private void doWithBytes() {
		throw new UnsupportedOperationException("Operate bytes directly is not allowed.");
	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.opsForHash().hasKey(region,key);
	}

	@Override
	public Collection<String> keys() {
		return 	redisTemplate.keys(region + ":*");
	}

	@Override
	public void evict(String... keys) {
		List<String> strings = Arrays.asList(keys);
		redisTemplate.delete(strings);
	}

	@Override
	public void clear() {
		redisTemplate.delete(keys());
	}

	@Override
	public Object get(String key) {
		return redisTemplate.opsForValue().get(getFullKey(key));
	}

	@Override
	public Map<String, Object> get(Collection<String> keys) {
		Map<String, Object> res = new HashMap<>();
		keys.forEach(k-> res.put(k, redisTemplate.opsForValue().get(getFullKey(k))));
		return res;
	}

	@Override
	public void put(String key, Object value) {
		redisTemplate.opsForValue().set(getFullKey(key),value);
	}

	@Override
	public void put(String key, Object value, long timeToLiveInSeconds) {
		redisTemplate.opsForValue().set(getFullKey(key),value,timeToLiveInSeconds, TimeUnit.SECONDS);
	}

	@Override
	public void put(Map<String, Object> elements) {
		elements.forEach((k,v)-> redisTemplate.opsForValue().set(getFullKey(k),v));
	}

	@Override
	public void put(Map<String, Object> elements, long timeToLiveInSeconds) {
		elements.forEach((k,v)-> redisTemplate.opsForValue().set(k,v,timeToLiveInSeconds,TimeUnit.SECONDS));
	}
}
