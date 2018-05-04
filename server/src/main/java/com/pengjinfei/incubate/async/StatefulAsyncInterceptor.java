package com.pengjinfei.incubate.async;

import com.pengjinfei.incubate.common.AbstractCachedInterceptor;
import com.pengjinfei.incubate.common.BeanAnnotation;
import com.pengjinfei.incubate.common.MethodAnnotationContext;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 * Created on 4/22/18
 *
 * @author jinfei
 */
@Component
public class StatefulAsyncInterceptor extends AbstractCachedInterceptor<StatefulAsync> {
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
