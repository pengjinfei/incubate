package com.pengjinfei.incubate.retry.registry;

import com.pengjinfei.incubate.retry.properties.AsyncRetryProperties;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface AsyncRetryRegistry {

    void register(String beanName, String methodName, AsyncRetryProperties properties);

    AsyncRetryProperties get(String beanName, String methodName);
}
