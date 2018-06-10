package com.pengjinfei.incubate.retry.policy;

import com.pengjinfei.incubate.retry.context.RetryContext;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface RetryPolicy {

    DelayTime getNextRetryDelayTime(RetryContext context);
}
