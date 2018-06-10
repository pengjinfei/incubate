package com.pengjinfei.incubate.step.context;

import com.pengjinfei.incubate.step.StepStatus;
import com.pengjinfei.incubate.step.chain.StepChain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class StepContext {

    private Map<String, Object> map = new HashMap<>();

    private boolean running = false;

    private StepStatus status;

    private String breakPointStep;

    private String key;

    private StepChain stepChain;

    public StepChain getStepChain() {
        return stepChain;
    }

    public void setStepChain(StepChain stepChain) {
        this.stepChain = stepChain;
    }

    private Exception lastException;

    public Exception getLastException() {
        return lastException;
    }

    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private <T> T get(String key, Class<T> tClass) {
        return (T) map.get(key);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }

    public String getBreakPointStep() {
        return breakPointStep;
    }

    public void setBreakPointStep(String breakPointStep) {
        this.breakPointStep = breakPointStep;
    }

    public void put(String key, Object value) {

    }
}
