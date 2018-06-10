package com.pengjinfei.incubate.step.chain;

import com.pengjinfei.incubate.common.KeyGenerator;
import com.pengjinfei.incubate.step.Named;
import com.pengjinfei.incubate.step.interceptor.StepInterceptor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class StepChain<T extends Serializable> implements Named {

    private StepNode<T> head;

    private List<StepInterceptor<T>> interceptors = new LinkedList<>();

    private String name;

    private KeyGenerator keyGenerator;

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public StepNode<T> getHead() {
        return head;
    }

    public void setHead(StepNode<T> head) {
        this.head = head;
    }

    public List<StepInterceptor<T>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<StepInterceptor<T>> interceptors) {
        this.interceptors = interceptors;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
