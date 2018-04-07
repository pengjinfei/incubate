package com.pengjinfei.incubate.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 4/7/18
 *
 * @author Pengjinfei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Locked {

    String value() default "";

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long time() default -1;
}
