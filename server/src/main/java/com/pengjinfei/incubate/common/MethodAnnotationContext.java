package com.pengjinfei.incubate.common;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
public class MethodAnnotationContext {

	private static ConcurrentHashMap<MethodClassKey, Map<Class<? extends Annotation>,BeanAnnotation>> attributeCache = new ConcurrentHashMap<>(16);

	public static <T extends Annotation> void cache(MethodClassKey key,Class<? extends Annotation> targetClass, T t, String beanName) {
		Map<Class<? extends Annotation>, BeanAnnotation> map = attributeCache.computeIfAbsent(key, k -> new HashMap<>(8));
		BeanAnnotation<T> beanAnnotation = new BeanAnnotation<>();
		beanAnnotation.setAnnotation(t);
		beanAnnotation.setBeanName(beanName);
		map.putIfAbsent(targetClass, beanAnnotation);
	}

	public static <T extends Annotation> BeanAnnotation<T> getBeanAnnotation(MethodClassKey key, Class<T> annoClass) {
		Map<Class<? extends Annotation>, BeanAnnotation> map = attributeCache.get(key);
		if (map == null) {
			return null;
		} else {
			return map.get(annoClass);
		}
	}

	public static <T extends Annotation> BeanAnnotation<T> getBeanAnnotation(MethodInvocation invocation,Class<T> aClass) {
		Method method = invocation.getMethod();
		Class<?> targetClass = AopUtils.getTargetClass(invocation.getThis());
		return MethodAnnotationContext.getBeanAnnotation(new MethodClassKey(method, targetClass), aClass);
	}

	public static boolean contain(MethodClassKey key, Class<? extends Annotation> targetClass) {
		Map<Class<? extends Annotation>, BeanAnnotation> map = attributeCache.get(key);
		if (map == null) {
			return false;
		}
		return map.containsKey(targetClass);
	}
}
