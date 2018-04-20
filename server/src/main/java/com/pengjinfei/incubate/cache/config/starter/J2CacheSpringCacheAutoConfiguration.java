package com.pengjinfei.incubate.cache.config.starter;

import com.pengjinfei.incubate.cache.config.J2CacheCacheManger;
import com.pengjinfei.incubate.cache.config.J2cacheRedisUtils;
import com.pengjinfei.incubate.cache.config.SpringRedisProvider;
import com.pengjinfei.incubate.cache.config.SpringRedisPubSubPolicy;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Properties;

import static com.pengjinfei.incubate.cache.config.RedisConstants.REDIS_TEMPLATE_BEAN_NAME;


/**
 * 开启对spring cache支持的配置入口
 * @author zhangsaizz
 *
 */
@Configuration
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({CacheProperties.class })
@EnableCaching
public class J2CacheSpringCacheAutoConfiguration extends CachingConfigurerSupport {

	private final CacheProperties cacheProperties;

	J2CacheSpringCacheAutoConfiguration(CacheProperties cacheProperties) {
		this.cacheProperties = cacheProperties;
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		List<String> cacheNames = cacheProperties.getCacheNames();
		J2CacheCacheManger cacheCacheManger = new J2CacheCacheManger(cacheChannel());
		cacheCacheManger.setCacheNames(cacheNames);
		return cacheCacheManger;
	}

	@Bean
	@ConditionalOnMissingBean(
			name = {REDIS_TEMPLATE_BEAN_NAME}
	)
	public RedisTemplate<String, Object> j2cacheRedisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		return redisTemplate;
	}


	@Bean
	@DependsOn("j2cacheRedisUtils")
	public CacheChannel cacheChannel() {
		J2CacheConfig cacheConfig = new J2CacheConfig();
		cacheConfig.setBroadcast(SpringRedisPubSubPolicy.class.getName());
		cacheConfig.setL1CacheName("ehcache3");
		cacheConfig.setL2CacheName(SpringRedisProvider.class.getName());
		cacheConfig.setSerialization("fst");
		Properties l1Properties = new Properties();
		l1Properties.setProperty("configXml", "/ehcache3.xml");
        l1Properties.setProperty("defaultHeapSize", "1000");
		cacheConfig.setL1CacheProperties(l1Properties);
		J2CacheBuilder builder = J2CacheBuilder.init(cacheConfig);
		return builder.getChannel();
	}

	@Bean
	public J2cacheRedisUtils j2cacheRedisUtils() {
		return new J2cacheRedisUtils();
	}

}
