package com.pengjinfei.incubate.step.chain;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface KeyGenerator<T> {

    String generateKey(T t);

}
