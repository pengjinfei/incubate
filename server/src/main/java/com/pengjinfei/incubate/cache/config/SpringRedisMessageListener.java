package com.pengjinfei.incubate.cache.config;

import com.alibaba.fastjson.JSON;
import net.oschina.j2cache.ClusterPolicy;
import net.oschina.j2cache.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author PENGJINFEI533
 * @since 2018-03-07
 */
public class SpringRedisMessageListener implements MessageListener {

	private static Logger logger = LoggerFactory.getLogger(SpringRedisMessageListener.class);

	private ClusterPolicy clusterPolicy;

	private String channel;

	private RedisTemplate<String, Object> redisTemplate;

	SpringRedisMessageListener(ClusterPolicy clusterPolicy, String channel){
		this.clusterPolicy = clusterPolicy;
		this.channel = channel;
		this.redisTemplate = J2cacheRedisUtils.getRedisTemplate();
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		byte[] messageChannel = message.getChannel();
		byte[] messageBody = message.getBody();
		if (messageChannel == null || messageBody == null) {
			return;
		}
		try {
			Object deserialize = redisTemplate.getValueSerializer().deserialize(messageBody);
			Command cmd = JSON.parseObject(deserialize.toString(), Command.class);
			if (cmd == null || cmd.isLocal())
				return;

			switch (cmd.getOperator()) {
				case Command.OPT_JOIN:
					logger.info("Node-"+cmd.getSrc()+" joined to " + this.channel);
					break;
				case Command.OPT_EVICT_KEY:
					clusterPolicy.evict(cmd.getRegion(), cmd.getKeys());
					logger.debug("Received cache evict message, region=" + cmd.getRegion() + ",key=" + String.join(",", cmd.getKeys()));
					break;
				case Command.OPT_CLEAR_KEY:
					clusterPolicy.clear(cmd.getRegion());
					logger.debug("Received cache clear message, region=" + cmd.getRegion());
					break;
				case Command.OPT_QUIT:
					logger.info("Node-"+cmd.getSrc()+" quit to " + this.channel);
					break;
				default:
					logger.warn("Unknown message type = " + cmd.getOperator());
			}
		} catch (Exception e) {
			logger.error("Failed to handle received msg", e);
		}
	}

}
