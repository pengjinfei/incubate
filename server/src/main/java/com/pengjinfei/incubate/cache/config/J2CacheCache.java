package com.pengjinfei.incubate.cache.config;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * {@link CacheManager} implementation for J2Cache.
 * @author zhangsaizz
 *
 */
public class J2CacheCache extends AbstractValueAdaptingCache {

	private static Logger logger = LoggerFactory.getLogger(J2CacheCache.class);

	private CacheChannel cacheChannel;

	private String j2CacheName = "j2cache";

	public J2CacheCache(String cacheName, CacheChannel cacheChannel) {
		this(cacheName,cacheChannel, true);
	}

	public J2CacheCache(String cacheName, CacheChannel cacheChannel, boolean allowNullValues) {
		super(allowNullValues);
		j2CacheName = cacheName;
		this.cacheChannel = cacheChannel;
	}

	@Override
	public String getName() {
		return this.j2CacheName;
	}

	public void setJ2CacheNmae(String name) {
		this.j2CacheName = name;
	}

	@Override
	public Object getNativeCache() {
		return this.cacheChannel;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		T value;
		try {
			value = valueLoader.call();
		} catch (Throwable ex) {
			throw new ValueRetrievalException(key, valueLoader, ex);
		}
		put(key, value);
		return value;
	}

	@Override
	public void put(Object key, Object value) {
		cacheChannel.set(j2CacheName, String.valueOf(key), (Serializable) toStoreValue(value));
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		if (!cacheChannel.exists(j2CacheName, String.valueOf(key))) {
			cacheChannel.set(j2CacheName, String.valueOf(key), (Serializable) value);
		}
		return get(key);
	}

	@Override
	public void evict(Object key) {
		cacheChannel.evict(j2CacheName, String.valueOf(key));
	}

	@Override
	public void clear() {
		cacheChannel.clear(j2CacheName);
	}

	@Override
	protected Object lookup(Object key) {
		CacheObject cacheObject = cacheChannel.get(j2CacheName, String.valueOf(key));
		return getValueByCacheObject(cacheObject);
	}

	private Object getValueByCacheObject(CacheObject cacheObject) {
		if (cacheObject != null) {
			return cacheObject.getValue();
		}
		return null;
	}

}
