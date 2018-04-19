package com.pengjinfei.incubate.lock;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
public interface Lock {

    void release(String path);

    boolean tryLock(String path);
}
