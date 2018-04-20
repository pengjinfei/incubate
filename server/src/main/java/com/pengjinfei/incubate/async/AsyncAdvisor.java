package com.pengjinfei.incubate.async;

import com.pengjinfei.incubate.common.AbstractCachedInterceptor;
import com.pengjinfei.incubate.common.BeanAnnotation;
import com.pengjinfei.incubate.common.CommonAnnotationMethodPointcut;
import com.pengjinfei.incubate.common.MethodAnnotationContext;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author PENGJINFEI533
 * @since 2018-04-18
 */
@Slf4j
@Component
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AsyncAdvisor extends AbstractPointcutAdvisor {

	@Override
	public Pointcut getPointcut() {
		return new CommonAnnotationMethodPointcut(StatefulAsync.class);
	}

	@Override
	public Advice getAdvice() {
		return new StatefulAsyncInterceptor();
	}

	private class StatefulAsyncInterceptor extends AbstractCachedInterceptor<StatefulAsync> {
		@Override
		protected Object doWithFound(MethodInvocation invocation, StatefulAsync found) throws Throwable {
			if (AsyncRunningContext.isRunInScheduler()) {
				return invocation.proceed();
			}
			BeanAnnotation<StatefulAsync> beanAnnotation = MethodAnnotationContext.getBeanAnnotation(invocation, StatefulAsync.class);
			String beanName = beanAnnotation.getBeanName();
			AsyncPayload payload = new AsyncPayload();
			payload.setBeanName(beanName);
			payload.setArgs(invocation.getArguments());
			payload.setMethodName(invocation.getMethod().getName());
			StatefulAsyncService.async(payload);
			return null;
		}
	}


	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 900;
	}
}

