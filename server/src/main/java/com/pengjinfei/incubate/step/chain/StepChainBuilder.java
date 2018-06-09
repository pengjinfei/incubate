package com.pengjinfei.incubate.step.chain;

import com.pengjinfei.incubate.step.Step;
import com.pengjinfei.incubate.step.repository.StepRepository;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class StepChainBuilder<T extends Serializable> {

    private StepChain<T> head;

    private String name;

    private StepRepository stepRepository;

    public StepChainBuilder<T> setStepRepository(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
        return this;
    }

    public StepChainBuilder<T> addStep(Step<T> step) {
        StepChain<T> node = new StepChain<>(step);
        if (head == null) {
            head = node;
        } else {
            StepChain<T> tail = head;
            while (tail.getNext() != null) {
                tail = tail.getNext();
            }
            tail.setNext(node);
        }
        return this;
    }

    public StepChainBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public StepChain<T> build() {
        if (head != null) {
            head.setName(name);
            stepRepository.registerStepChain(head);
        }
        return head;
    }

}
