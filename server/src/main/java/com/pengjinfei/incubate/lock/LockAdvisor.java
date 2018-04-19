package com.pengjinfei.incubate.lock;

import com.pengjinfei.incubate.common.AbstractCachedInterceptor;
import com.pengjinfei.incubate.common.CommonAnnotationMethodPointcut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
@Slf4j
@RequiredArgsConstructor
public class LockAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final Lock lock;

    @Override
    public Pointcut getPointcut() {
        return new CommonAnnotationMethodPointcut(Locked.class);
    }

    @Override
    public Advice getAdvice() {
        return new LockInterceptor();
    }

    private class LockInterceptor extends AbstractCachedInterceptor<Locked>{

        @Override
        protected Object doWithFound(MethodInvocation invocation, Locked locked) throws Throwable{
            String path = locked.value();
            if (StringUtils.isEmpty(path)) {
                path = invocation.getThis().getClass().getSimpleName() + "_" + invocation.getMethod().getName();
            }
            boolean locker;
            try {
                locker = lock.tryLock(path);
            } catch (Exception e) {
                log.error(String.format("get lock for path %s error", path), e);
                return null;
            }
            if (!locker) {
                return null;
            }
            try {
                return invocation.proceed();
            } finally {
                lock.release(path);
            }
        }
    }

}
