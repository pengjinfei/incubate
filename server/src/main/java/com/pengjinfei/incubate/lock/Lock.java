package com.pengjinfei.incubate.lock;

import java.util.concurrent.TimeUnit;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
public interface Lock {

    boolean getLock(String path);

    boolean getLock(String path, long time, TimeUnit unit);

    void release(String path);
}
