package com.pengjinfei.incubate.common;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
@RequiredArgsConstructor
public class CommonAnnotationMethodPointcut extends StaticMethodMatcherPointcut {

	private final Class<? extends Annotation> annotationType;

	@Override
	public ClassFilter getClassFilter() {
		return clazz -> ProxyCreationContext.getCurrentProxiedBeanName() != null;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		MethodClassKey methodClassKey = new MethodClassKey(method, targetClass);
		if (MethodAnnotationContext.contain(methodClassKey, annotationType)) {
			return true;
		}
		String beanName = ProxyCreationContext.getCurrentProxiedBeanName();
		Class<?> userClass = ClassUtils.getUserClass(targetClass);
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		Annotation annotation = specificMethod.getAnnotation(annotationType);
		if (annotation == null) {
			annotation = method.getAnnotation(annotationType);
		}
		if (annotation == null) {
			return false;
		}
		MethodAnnotationContext.cache(methodClassKey,annotationType, annotation,beanName);
		return true;
	}

}
