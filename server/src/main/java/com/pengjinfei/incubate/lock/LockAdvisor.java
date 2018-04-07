package com.pengjinfei.incubate.lock;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
public class LockAdvisor extends AbstractPointcutAdvisor {

    @Autowired
    private Lock lock;


    @Override
    public Pointcut getPointcut() {
        return new AnnotationMatchingPointcut(Locked.class);
    }

    @Override
    public Advice getAdvice() {
        return new LockInterceptor();
    }

    private class LockInterceptor implements MethodInterceptor{

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Object target = invocation.getThis();
            Locked locked = AnnotationUtils.findAnnotation(method, Locked.class);
            if (locked == null) {
                Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                locked = AnnotationUtils.findAnnotation(targetMethod, Locked.class);
            }
            if (locked == null) {
                return invocation.proceed();
            }
            String path = target.getClass().getName() + "_" + method.getName();
            boolean locker;
            try {
                if (locked.time() > 0) {
                    locker = LockAdvisor.this.lock.getLock(path, locked.time(), locked.timeUnit());
                } else {
                    locker = lock.getLock(path);
                }
            } catch (Exception e) {
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
