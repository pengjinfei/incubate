package com.pengjinfei.incubate.retry.operations;

import com.pengjinfei.incubate.retry.AsyncRetryMeta;
import com.pengjinfei.incubate.retry.callback.AsyncRetryCallback;
import com.pengjinfei.incubate.retry.context.RetryContext;
import com.pengjinfei.incubate.retry.context.RetryContextDao;
import com.pengjinfei.incubate.retry.delay.DelayCallback;
import com.pengjinfei.incubate.retry.delay.Delayor;
import com.pengjinfei.incubate.retry.policy.DelayTime;
import com.pengjinfei.incubate.retry.policy.RetryPolicy;
import com.pengjinfei.incubate.retry.properties.AsyncRetryProperties;
import com.pengjinfei.incubate.retry.registry.AsyncRetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
@Component
@Slf4j
public class AsyncRetryTemplate implements AsyncRetryOperations,ApplicationContextAware,DelayCallback<AsyncRetryMeta> {

    private AsyncRetryRegistry asyncRetryRegistry;

    private RetryContextDao retryContextDao;

    private Delayor<AsyncRetryMeta> delayor;

    @Autowired
    public void setDelayor(Delayor<AsyncRetryMeta> delayor) {
        this.delayor = delayor;
    }

    @Autowired
    public void setRetryContextDao(RetryContextDao retryContextDao) {
        this.retryContextDao = retryContextDao;
    }

    @Autowired
    public void setAsyncRetryRegistry(AsyncRetryRegistry asyncRetryRegistry) {
        this.asyncRetryRegistry = asyncRetryRegistry;
    }

    @Override
    public Object runAndCatch(AsyncRetryMeta asyncRetryMeta, boolean isFirstTime) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object bean = applicationContext.getBean(asyncRetryMeta.getBeanName());
        Class<?> userClass = ClassUtils.getUserClass(bean);
        Class<?>[] paramType = new Class[asyncRetryMeta.getArgs().length];
        for (int j = 0; j < paramType.length; j++) {
            paramType[j] = asyncRetryMeta.getArgs()[j].getClass();
        }
        RetryContext context = null;
        if (!isFirstTime) {
            context = retryContextDao.selectByMeta(asyncRetryMeta);
        }
        if (context == null) {
            context = new RetryContext();
            List<Date> retryTimes = new LinkedList<>();
            context.setRetryTimes(retryTimes);
        }
        context.setMeta(asyncRetryMeta);
        AsyncRetryProperties retryProperties = asyncRetryRegistry.get(asyncRetryMeta.getBeanName(), asyncRetryMeta.getMethodName());
        AsyncRetryCallback callback = retryProperties.getCallback();
        try {
            Method method = userClass.getMethod(asyncRetryMeta.getMethodName(), paramType);
            method.setAccessible(true);
            Object res = method.invoke(bean, asyncRetryMeta.getArgs());
            if (callback != null) {
                try {
                    callback.success(context);
                } catch (Exception e) {
                    log.error("success callback failed.", e);
                }
            }
            return res;
        } catch (Exception e) {
            context.setLastException(e);
            boolean exRetry = true;
            Throwable throwable = e;
            if (e instanceof NoSuchMethodException || e instanceof IllegalAccessException) {
                exRetry = false;
            } else if (e instanceof InvocationTargetException) {
                throwable = ((InvocationTargetException) e).getTargetException();
            }
            if (exRetry) {
                exRetry = retryFor(retryProperties, throwable);
            }
            boolean failFinal = true;
            if (exRetry) {
                RetryPolicy retryPolicy = retryProperties.getRetryPolicy();
                DelayTime nextRetryDelayTime = retryPolicy.getNextRetryDelayTime(context);
                if (nextRetryDelayTime != null) {
                    context.getRetryTimes().add(new Date());
                    context.setLastDelayTime(nextRetryDelayTime);
                    retryContextDao.save(context);
                    failFinal = false;
                    delayor.delay(asyncRetryMeta, nextRetryDelayTime);
                    if (callback != null) {
                        try {
                            callback.failOnce(context);
                        } catch (Exception e1) {
                            log.error("failonce callback failed.", e1);
                        }
                    }
                }
            }

            if (failFinal && callback !=null) {
                try {
                    callback.failFinal(context);
                } catch (Exception e1) {
                    log.error("failFinal callback failed", e1);
                }
            }

            if (failFinal && !isFirstTime) {
                retryContextDao.delete(context);
            }

            if (isFirstTime) {
                throw e;
            }
        }
        return null;
    }

    private boolean retryFor(AsyncRetryProperties meta, Throwable ex) {
        RollbackRuleAttribute winner = null;
        int deepest = Integer.MAX_VALUE;
        List<RollbackRuleAttribute> retryRules = meta.getRetryRules();
        if (retryRules != null) {
            for (RollbackRuleAttribute rule : retryRules) {
                int depth = rule.getDepth(ex);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    winner = rule;
                }
            }
        }

        if (winner == null) {
            return ex instanceof RuntimeException || ex instanceof Error;
        } else {
            return !(winner instanceof NoRollbackRuleAttribute);
        }
    }

    @Override
    public void asyncRun(AsyncRetryMeta asyncRetryMeta) {
        AsyncRetryProperties retryProperties = asyncRetryRegistry.get(asyncRetryMeta.getBeanName(), asyncRetryMeta.getMethodName());
        RetryPolicy retryPolicy = retryProperties.getRetryPolicy();
        DelayTime nextRetryDelayTime = retryPolicy.getNextRetryDelayTime(null);
        delayor.delay(asyncRetryMeta,nextRetryDelayTime);
    }

    @PostConstruct
    public void init() {
        delayor.setCallback(this);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void callback(AsyncRetryMeta param) throws Exception {
        runAndCatch(param, false);
    }
}
