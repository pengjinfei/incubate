package com.pengjinfei.incubate.step;

import com.pengjinfei.incubate.step.context.StepContext;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface RollbackableStep<T extends Serializable> extends Step<T> {

    void rollback(T t, StepContext context);
}
