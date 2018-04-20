package com.pengjinfei.incubate.cache.config;

import net.oschina.j2cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PENGJINFEI533
 * @since 2018-03-07
 */
public class SpringRedisProvider implements CacheProvider {

	private final static Logger log = LoggerFactory.getLogger(SpringRedisProvider.class);

	private String namespace = "j2cache";

	protected ConcurrentHashMap<String, SpringRedisCache> caches = new ConcurrentHashMap<>();

	@Override
	public String name() {
		return "redis";
	}

	@Override
	public int level() {
		return CacheObject.LEVEL_2;
	}

	@Override
	public Collection<CacheChannel.Region> regions() {
		return null;
	}

	@Override
	public Cache buildCache(String region, CacheExpiredListener listener) {
		SpringRedisCache cache = caches.get(region);
		if (cache == null) {
			synchronized (SpringRedisProvider.class) {
				if (cache == null) {
					cache = new SpringRedisCache(this.namespace, region);
					caches.put(region, cache);
				}
			}
		}
		return cache;
	}

	@Override
	public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
		return buildCache(region, listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Properties props) {
		this.namespace = props.getProperty("namespace");
	}

	@Override
	public void stop() {
		// 由spring控制
	}

}
