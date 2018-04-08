package com.pengjinfei.incubate.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private class LockInterceptor implements MethodInterceptor{

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
                path = target.getClass().getName() + "_" + method.getName();
            }
            boolean locker;
            try {
                locker = lock.tryLock(path);
            } catch (Exception e) {
                log.error(String.format("get lock for path %s error",path), e);
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

    private final class AnnotationClassOrMethodPointcut extends StaticMethodMatcherPointcut {

        private final MethodMatcher methodResolver;

        AnnotationClassOrMethodPointcut(Class<? extends Annotation> annotationType) {
            this.methodResolver = new AnnotationMethodMatcher(annotationType);
            setClassFilter(new AnnotationClassOrMethodFilter(annotationType));
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return getClassFilter().matches(targetClass) || this.methodResolver.matches(method, targetClass);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationClassOrMethodPointcut)) {
                return false;
            }
            AnnotationClassOrMethodPointcut otherAdvisor = (AnnotationClassOrMethodPointcut) other;
            return ObjectUtils.nullSafeEquals(this.methodResolver, otherAdvisor.methodResolver);
        }

    }

    private final class AnnotationClassOrMethodFilter extends AnnotationClassFilter {

        private final AnnotationMethodsResolver methodResolver;

        AnnotationClassOrMethodFilter(Class<? extends Annotation> annotationType) {
            super(annotationType, true);
            this.methodResolver = new AnnotationMethodsResolver(annotationType);
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return super.matches(clazz) || this.methodResolver.hasAnnotatedMethods(clazz);
        }

    }

    private static class AnnotationMethodsResolver {

        private Class<? extends Annotation> annotationType;

        public AnnotationMethodsResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean hasAnnotatedMethods(Class<?> clazz) {
            final AtomicBoolean found = new AtomicBoolean(false);
            ReflectionUtils.doWithMethods(clazz,
                    new ReflectionUtils.MethodCallback() {
                        @Override
                        public void doWith(Method method) throws IllegalArgumentException,
                                IllegalAccessException {
                            if (found.get()) {
                                return;
                            }
                            Annotation annotation = AnnotationUtils.findAnnotation(method,
                                    annotationType);
                            if (annotation != null) { found.set(true); }
                        }
                    });
            return found.get();
        }

    }

}
