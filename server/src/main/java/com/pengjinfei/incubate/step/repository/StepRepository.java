package com.pengjinfei.incubate.step.repository;

import com.pengjinfei.incubate.step.chain.StepChain;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface StepRepository {

    void registerStepChain(StepChain stepChain);

    StepChain getStepChain(String name);
}
