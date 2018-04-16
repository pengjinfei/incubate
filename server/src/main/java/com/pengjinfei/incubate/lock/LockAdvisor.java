package com.pengjinfei.incubate.lock;

import com.pengjinfei.incubate.common.AnnotationClassOrMethodPointcut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
@Slf4j
@RequiredArgsConstructor
public class LockAdvisor extends AbstractPointcutAdvisor {

    private final Lock lock;

    private final Map<Object, Map<Method, Locked>> delegates =
            new HashMap<>();

    @Override
    public Pointcut getPointcut() {
        return new AnnotationClassOrMethodPointcut(Locked.class);
    }

    @Override
    public Advice getAdvice() {
        return new LockInterceptor();
    }

    private class LockInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Object target = invocation.getThis();
            Locked locked;
            if (delegates.containsKey(target) && delegates.get(target).containsKey(method)) {
                locked = delegates.get(target).get(method);
            } else {
                synchronized (delegates) {
                    if (!delegates.containsKey(target)) {
                        delegates.put(target, new HashMap<>(8));
                    }
                    Map<Method, Locked> lockedMap = delegates.get(target);
                    locked = AnnotationUtils.findAnnotation(method, Locked.class);
                    if (locked == null) {
                        Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                        locked = AnnotationUtils.findAnnotation(targetMethod, Locked.class);
                    }
                    lockedMap.put(method, locked);
                }
            }
            if (locked == null) {
                return invocation.proceed();
            }
            String path = locked.value();
            if (StringUtils.isEmpty(path)) {
                path = target.getClass().getSimpleName() + "_" + method.getName();
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
