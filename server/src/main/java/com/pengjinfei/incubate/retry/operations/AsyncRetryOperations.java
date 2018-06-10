package com.pengjinfei.incubate.retry.operations;

import com.pengjinfei.incubate.retry.AsyncRetryMeta;

import java.lang.reflect.InvocationTargetException;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public interface AsyncRetryOperations {

    Object runAndCatch(AsyncRetryMeta asyncRetryMeta) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    void asyncRun(AsyncRetryMeta asyncRetryMeta);
}
