package com.pengjinfei.incubate.common;

import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author PENGJINFEI533
 * @since 2018-04-16
 */
public class AnnotationClassOrMethodPointcut extends StaticMethodMatcherPointcut {

	private final MethodMatcher methodResolver;

	public AnnotationClassOrMethodPointcut(Class<? extends Annotation> annotationType) {
		this.methodResolver = new AnnotationMethodMatcher(annotationType);
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return this.methodResolver.matches(method, targetClass);
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


	private static class AnnotationMethodsResolver {

		private Class<? extends Annotation> annotationType;

		AnnotationMethodsResolver(Class<? extends Annotation> annotationType) {
			this.annotationType = annotationType;
		}

		boolean hasAnnotatedMethods(Class<?> clazz) {
			final AtomicBoolean found = new AtomicBoolean(false);
			ReflectionUtils.doWithMethods(clazz,
					method -> {
						if (found.get()) {
							return;
						}
						Annotation annotation = AnnotationUtils.findAnnotation(method,
								annotationType);
						if (annotation != null) { found.set(true); }
					});
			return found.get();
		}

	}
}
