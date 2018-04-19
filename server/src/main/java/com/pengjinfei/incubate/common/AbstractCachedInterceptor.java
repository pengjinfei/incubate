package com.pengjinfei.incubate.common;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author PENGJINFEI533
 * @since 2018-04-18
 */
public abstract class AbstractCachedInterceptor<T extends Annotation> implements MethodInterceptor {

	private Class<T> aClass;

	{
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		aClass = (Class<T>) actualTypeArguments[0];
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		BeanAnnotation<T> beanAnnotation = MethodAnnotationContext.getBeanAnnotation(invocation, aClass);
		if (beanAnnotation == null) {
			return invocation.proceed();
		}
		T found = beanAnnotation.getAnnotation();
		if (found == null) {
			return invocation.proceed();
		} else {
			return doWithFound(invocation, found);
		}
	}

	protected abstract Object doWithFound(MethodInvocation invocation, T found) throws Throwable;
}
