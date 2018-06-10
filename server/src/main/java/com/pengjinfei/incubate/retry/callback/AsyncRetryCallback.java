package com.pengjinfei.incubate.retry.callback;

import com.pengjinfei.incubate.retry.context.RetryContext;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface AsyncRetryCallback {

    void failOnce(RetryContext context, Object[] args);

    void success(RetryContext context, Object[] args);

    void failFinal(RetryContext context, Object[] args);

}
