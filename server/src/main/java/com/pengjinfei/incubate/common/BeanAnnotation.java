package com.pengjinfei.incubate.common;

import lombok.Data;

import java.lang.annotation.Annotation;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
@Data
public class BeanAnnotation<T extends Annotation> {

	private String beanName;
	private T annotation;
}
