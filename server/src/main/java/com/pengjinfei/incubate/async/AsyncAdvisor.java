package com.pengjinfei.incubate.async;

import com.pengjinfei.incubate.common.CommonAnnotationMethodPointcut;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class AsyncAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	@Override
	public Pointcut getPointcut() {
		return new CommonAnnotationMethodPointcut(StatefulAsync.class);
	}

	@Override
	@Autowired
	@Qualifier("statefulAsyncInterceptor")
	public void setAdvice(Advice advice) {
		super.setAdvice(advice);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 900;
	}
}

