package com.pengjinfei.incubate.cache.config;

import net.oschina.j2cache.ClusterPolicy;
import net.oschina.j2cache.Command;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Properties;

/**
 * @author PENGJINFEI533
 * @since 2018-03-07
 */
public class SpringRedisPubSubPolicy implements ClusterPolicy {

	private RedisTemplate<String, Object> redisTemplate;

	private String channel = "j2cache_channel";

	@SuppressWarnings("unchecked")
	@Override
	public void connect(Properties props) {
		String channelName = props.getProperty("jgroups.channel.name");
		if(channelName != null && !channelName.isEmpty()) {
			this.channel = channelName;
		}
		this.redisTemplate = J2cacheRedisUtils.getRedisTemplate();
		RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
		listenerContainer.setConnectionFactory(J2cacheRedisUtils.getRedisConnectionFactory());
		listenerContainer.addMessageListener(new SpringRedisMessageListener(this, this.channel), new PatternTopic(this.channel));
		listenerContainer.afterPropertiesSet();
		listenerContainer.start();
	}

	@Override
	public void sendEvictCmd(String region, String... keys) {
        Command cmd = new Command(Command.OPT_EVICT_KEY, region, keys);
        redisTemplate.convertAndSend(this.channel, cmd.json());
	}

	@Override
	public void sendClearCmd(String region) {
        Command cmd = new Command(Command.OPT_CLEAR_KEY, region, "");
		redisTemplate.convertAndSend(this.channel, cmd.json());
	}

	@Override
	public void disconnect() {
		redisTemplate.convertAndSend(this.channel, Command.quit().json());
	}

}
