package com.pengjinfei.incubate.async;

import lombok.extern.slf4j.Slf4j;
import org.quartz.simpl.SimpleThreadPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created on 4/5/18
 *
 * @author Pengjinfei
 */
@Component
@Slf4j
public class AsyncScheduler implements Runnable,InitializingBean,SmartLifecycle {

    @Value("${poolSize}")
    private int threadPoolSize;

    private SimpleThreadPool threadPool;

    private volatile boolean isRunning = false;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private static final String LIST_KEY = "asyncSchedulerList";

    private static final String ZSET_KEY = "asyncSchedulerZset";

    @Override
    public void run() {
        while (true) {
            if (!isRunning) {
                return;
            }
            try {
                int threads = threadPool.blockForAvailableThreads();
                if (!isRunning) {
                    return;
                }
                AsyncMeta asyncMeta= (AsyncMeta) redisTemplate.opsForList().leftPop(LIST_KEY, 1, TimeUnit.MINUTES);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //阻塞等待 worker 线程可用
        //取 n个数据
        //如果没有取到 休眠随机时间

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        threadPool = new SimpleThreadPool(threadPoolSize,5);
    }

    @Override
    public void start() {
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        callback.run();
        isRunning = false;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
