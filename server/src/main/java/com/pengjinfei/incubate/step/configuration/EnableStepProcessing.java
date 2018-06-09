package com.pengjinfei.incubate.step.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SimpleStepConfiguration.class)
public @interface EnableStepProcessing {
}
