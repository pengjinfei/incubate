package com.pengjinfei.incubate.step.exception;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class RetryableException extends RuntimeException {

    public RetryableException(Throwable cause) {
        super(cause);
    }
}
