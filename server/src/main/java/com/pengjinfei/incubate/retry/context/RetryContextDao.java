package com.pengjinfei.incubate.retry.context;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface RetryContextDao {

    RetryContext select(String key);

    void save(RetryContext context);

    void delete(String key);
}
