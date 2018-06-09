package com.pengjinfei.incubate.step.chain;

import com.pengjinfei.incubate.step.repository.StepRepository;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class StepChainBuilderFactory {

    private final StepRepository stepRepository;

    public StepChainBuilderFactory(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    public <T extends Serializable> StepChainBuilder<T> ofType(Class<T> tClass) {
        return new StepChainBuilder<T>().setStepRepository(stepRepository);
    }
}
