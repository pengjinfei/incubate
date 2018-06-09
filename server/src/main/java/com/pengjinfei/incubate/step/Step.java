package com.pengjinfei.incubate.step;

import com.pengjinfei.incubate.step.context.StepContext;
import com.pengjinfei.incubate.step.exception.RetryableException;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface Step<T extends Serializable> extends Named{

    void preCommit(T t, StepContext context) throws RetryableException;

}
