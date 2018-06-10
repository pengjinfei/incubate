package com.pengjinfei.incubate.retry.callback;

import com.pengjinfei.incubate.retry.context.RetryContext;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface AsyncRetryCallback {

    void failOnce(RetryContext context);

    void success(RetryContext context);

    void failFinal(RetryContext context);

}
