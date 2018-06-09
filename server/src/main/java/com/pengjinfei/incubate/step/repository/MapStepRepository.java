package com.pengjinfei.incubate.step.repository;

import com.pengjinfei.incubate.step.chain.StepChain;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class MapStepRepository implements StepRepository {

    private ConcurrentHashMap<String, StepChain> map = new ConcurrentHashMap<>();

    @Override
    public void registerStepChain(StepChain stepChain) {
        StepChain old = map.putIfAbsent(stepChain.getName(), stepChain);
        if (old != null) {
            throw new RuntimeException("duplicate stepChain name " + stepChain.getName());
        }
    }

    @Override
    public StepChain getStepChain(String name) {
        return map.get(name);
    }
}
