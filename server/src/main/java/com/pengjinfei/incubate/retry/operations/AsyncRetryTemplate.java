package com.pengjinfei.incubate.retry.operations;

import com.pengjinfei.incubate.retry.AsyncRetryMeta;
import com.pengjinfei.incubate.retry.context.RetryContext;
import com.pengjinfei.incubate.retry.context.RetryContextDao;
import com.pengjinfei.incubate.retry.delay.DelayCallback;
import com.pengjinfei.incubate.retry.delay.Delayor;
import com.pengjinfei.incubate.retry.policy.DelayTime;
import com.pengjinfei.incubate.retry.policy.RetryPolicy;
import com.pengjinfei.incubate.retry.properties.AsyncRetryProperties;
import com.pengjinfei.incubate.retry.registry.AsyncRetryRegistry;
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
    public Object runAndCatch(AsyncRetryMeta asyncRetryMeta) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object bean = applicationContext.getBean(asyncRetryMeta.getBeanName());
        Class<?> userClass = ClassUtils.getUserClass(bean);
        Class<?>[] paramType = new Class[asyncRetryMeta.getArgs().length];
        for (int j = 0; j < paramType.length; j++) {
            paramType[j] = asyncRetryMeta.getArgs()[j].getClass();
        }
        try {
            Method method = userClass.getMethod(asyncRetryMeta.getMethodName(), paramType);
            method.setAccessible(true);
            return method.invoke(bean, asyncRetryMeta.getArgs());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw e;
        } catch (Exception e) {
            AsyncRetryProperties retryProperties = asyncRetryRegistry.get(asyncRetryMeta.getBeanName(), asyncRetryMeta.getMethodName());
            boolean exRetry = retryFor(retryProperties, e);
            if (exRetry) {
                RetryContext context = new RetryContext();
                context.setLastException(e);
                List<Date> retryTimes = new LinkedList<>();
                retryTimes.add(new Date());
                context.setRetryTimes(retryTimes);
                retryContextDao.save(context);
                RetryPolicy retryPolicy = retryProperties.getRetryPolicy();
                DelayTime nextRetryDelayTime = retryPolicy.getNextRetryDelayTime(context);
                delayor.delay(asyncRetryMeta,nextRetryDelayTime);
            }
            throw e;
        }
    }

    public boolean retryFor(AsyncRetryProperties meta, Throwable ex) {
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
    public void callback(AsyncRetryMeta param) {

    }
}
