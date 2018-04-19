package com.pengjinfei.incubate.async;

import com.pengjinfei.incubate.constants.RedisConstant;
import com.pengjinfei.incubate.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.simpl.SimpleThreadPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pengjinfei.incubate.constants.RedisConstant.LIST_KEY;
import static com.pengjinfei.incubate.constants.RedisConstant.ZSET_KEY;

/**
 * Created on 4/5/18
 *
 * @author Pengjinfei
 */
@Component
@Slf4j
public class AsyncScheduler implements Runnable,InitializingBean,SmartLifecycle,ApplicationContextAware {

    @Value("${poolSize}")
    private int threadPoolSize;

    private SimpleThreadPool threadPool;

    private volatile boolean isRunning = false;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RedisService redisService;

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
                for (int i = 0; i < threads; i++) {
                    AsyncPayload asyncPayload = (AsyncPayload) redisTemplate.opsForList().leftPop(LIST_KEY, 5, TimeUnit.SECONDS);
                    if (asyncPayload == null) {
                        break;
                    }
                    Object bean = ctx.getBean(asyncPayload.getBeanName());
                    Class<?>[] paramType = new Class[asyncPayload.getArgs().length];
					for (int j = 0; j < paramType.length; j++) {
						paramType[j] = asyncPayload.getArgs()[i].getClass();
					}
                    Class<?> userClass = ClassUtils.getUserClass(bean);
                    Method method = userClass.getMethod(asyncPayload.getMethodName(), paramType);
                    threadPool.runInThread(() -> {
                        try {
                            AsyncRunningContext.setRunInScheduler(true);
                            method.invoke(bean, asyncPayload.getArgs());
                        } catch (Exception e) {
                            handleRetryIfNessery(asyncPayload, e);
                        }finally {
                            AsyncRunningContext.setRunInScheduler(null);
                        }
                    });
                }

                List<AsyncPayload> payloads = redisService.pollMultiFromZset(ZSET_KEY, threadPoolSize, AsyncPayload.class);
                if (!CollectionUtils.isEmpty(payloads)) {
                	redisTemplate.executePipelined(new SessionCallback<Object>() {
                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                            for (AsyncPayload payload : payloads) {
                                operations.opsForList().rightPush((K)RedisConstant.LIST_KEY, (V)payload);
                            }
                            return null;
                        }
                    });
                }
            } catch (Exception e) {
                log.error("error occurred in scheduler loop.", e);
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException ie) {
                    log.error("scheduler sleep for retry is interrupted.");
                    return;
                }
            }
        }

    }


    private void handleRetryIfNessery(AsyncPayload payload, Exception e) {
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        threadPool = new SimpleThreadPool(threadPoolSize,5);
        threadPool.setThreadNamePrefix("asyncScheduler");
        threadPool.initialize();
    }

    @Override
    public void start() {
        isRunning = true;
        new Thread(this,"async-ractor").start();
    }

    @Override
    public void stop() {
        isRunning = false;
        threadPool.shutdown();
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

    private ApplicationContext ctx;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
