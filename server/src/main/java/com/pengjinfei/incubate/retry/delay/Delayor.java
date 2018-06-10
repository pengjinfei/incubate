package com.pengjinfei.incubate.retry.delay;

import com.pengjinfei.incubate.retry.policy.DelayTime;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface Delayor<T> {

    void delay(T params, DelayTime delayTime);

    void setCallback(DelayCallback<T> callback);
}
