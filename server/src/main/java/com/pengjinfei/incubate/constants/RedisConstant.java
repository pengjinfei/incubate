package com.pengjinfei.incubate.constants;

/**
 * @author PENGJINFEI533
 * @since 2018-04-18
 */
public interface RedisConstant {
	String LIST_KEY = "asyncSchedulerList";

	String ZSET_KEY = "asyncSchedulerZset";

	String RETRY_ZSET_KEY = "retrySchedulerZset";

	String LUA_ZSET2LIST = "zset2List";
}
