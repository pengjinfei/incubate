package com.pengjinfei.incubate.redis;

import com.pengjinfei.incubate.constants.RedisConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author PENGJINFEI533
 * @since 2017-08-31
 */
@Configuration
public class RedisScriptConfiguration {

    @Bean(RedisConstant.LUA_ZSET2LIST)
    public RedisScript<Long> listTransfer() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/transferZset2List.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
