package com.pengjinfei.incubate.lock;

import java.lang.annotation.*;

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
}
