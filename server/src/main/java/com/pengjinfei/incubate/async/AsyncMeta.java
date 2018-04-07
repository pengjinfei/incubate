package com.pengjinfei.incubate.async;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 4/6/18
 *
 * @author Pengjinfei
 */
@Data
public class AsyncMeta implements Serializable {
    private String beanName;
    private String methodName;
    private Object[] args;
}
