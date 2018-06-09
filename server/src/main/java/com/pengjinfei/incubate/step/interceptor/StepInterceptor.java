package com.pengjinfei.incubate.step.interceptor;

import com.pengjinfei.incubate.step.context.StepContext;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface StepInterceptor<T extends Serializable> {

    void beforeStep(T t, StepContext stepContext);

    void afterStep(T t, StepContext stepContext);
}
