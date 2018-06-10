package com.pengjinfei.incubate.retry.delay;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface DelayCallback<T> {

    void callback(T param) throws Exception;
}
