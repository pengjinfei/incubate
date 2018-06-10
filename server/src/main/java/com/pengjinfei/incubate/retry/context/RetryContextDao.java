package com.pengjinfei.incubate.retry.context;

import com.pengjinfei.incubate.retry.AsyncRetryMeta;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface RetryContextDao {

    RetryContext selectByMeta(AsyncRetryMeta meta);

    void save(RetryContext context);

    void delete(RetryContext context);
}
