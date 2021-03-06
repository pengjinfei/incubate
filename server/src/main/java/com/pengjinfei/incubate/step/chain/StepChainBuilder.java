package com.pengjinfei.incubate.step.chain;

import com.pengjinfei.incubate.step.Step;
import com.pengjinfei.incubate.step.interceptor.StepInterceptor;
import com.pengjinfei.incubate.step.repository.StepRepository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class StepChainBuilder<T extends Serializable> {

    private StepNode<T> head;

    private String name;

    private StepRepository stepRepository;

    private List<StepInterceptor<T>> interceptors = new LinkedList<>();

    private KeyGenerator keyGenerator;

    public StepChainBuilder<T> setStepRepository(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
        return this;
    }

    public StepChainBuilder<T> addStep(Step<T> step) {
        StepNode<T> node = new StepNode<>(step);
        if (head == null) {
            head = node;
        } else {
            StepNode<T> tail = head;
            while (tail.getNext() != null) {
                tail = tail.getNext();
            }
            tail.setNext(node);
        }
        return this;
    }

    public StepChainBuilder<T> addInterceptor(StepInterceptor<T> interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public StepChainBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public StepChainBuilder<T> key(KeyGenerator<T> keyGenerator) {
        this.keyGenerator = keyGenerator;
        return this;
    }

    public StepChain<T> build() {
        Assert.notNull(head,"there must have at least one step");
        Assert.notNull(name,"there must be a name for stepChain");
        Assert.notNull(keyGenerator,"there must be a keyGenerator for stepChain");
        StepChain<T> chain = new StepChain<>();
        chain.setName(name);
        chain.setHead(head);
        chain.setKeyGenerator(keyGenerator);
        if (!CollectionUtils.isEmpty(interceptors)) {
            chain.setInterceptors(interceptors);
        }
        stepRepository.registerStepChain(chain);
        return chain;
    }

}
