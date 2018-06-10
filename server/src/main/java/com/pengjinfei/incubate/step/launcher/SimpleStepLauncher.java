package com.pengjinfei.incubate.step.launcher;

import com.pengjinfei.incubate.step.StepStatus;
import com.pengjinfei.incubate.step.chain.StepChain;
import com.pengjinfei.incubate.step.context.StepContext;
import com.pengjinfei.incubate.step.context.StepContextDao;
import com.pengjinfei.incubate.step.interceptor.StepInterceptor;
import com.pengjinfei.incubate.step.repository.StepRepository;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class SimpleStepLauncher implements StepLauncher {

    private final StepContextDao stepContextDao;

    private final StepRepository stepRepository;

    public SimpleStepLauncher(StepContextDao stepContextDao, StepRepository stepRepository) {
        this.stepContextDao = stepContextDao;
        this.stepRepository = stepRepository;
    }

    @Override
    public <T extends Serializable> StepContext launch(StepChain<T> chain, T params, String key) throws Exception{
        StepContext lastContext = stepContextDao.select(key);
        if (lastContext != null) {
            lastContext.setRunning(false);
        } else {
            lastContext = new StepContext();
            lastContext.setRunning(true);
        }
        lastContext.setStatus(StepStatus.PREPARE);
        List<StepInterceptor<T>> interceptors = chain.getInterceptors();
        if (!CollectionUtils.isEmpty(interceptors)) {
            for (StepInterceptor<T> interceptor : interceptors) {
                interceptor.beforeStep(params,lastContext);
            }
        }
        Exception exception = null;
        try {
            chain.getHead().execute(params,lastContext);
            lastContext.setStatus(StepStatus.FINISHED);
            if (lastContext.getLastException() != null) {
                throw lastContext.getLastException();
            }
        } catch (Exception e) {
            exception = e;
        }
        if (!CollectionUtils.isEmpty(interceptors)) {
            for (StepInterceptor<T> interceptor : interceptors) {
                interceptor.afterStep(params,lastContext);
            }
        }
        if (exception != null) {
            throw exception;
        }
        return lastContext;
    }

    @Override
    public <T extends Serializable> StepContext launch(String chainName, T params, String key) throws Exception {
        StepChain stepChain = stepRepository.getStepChain(chainName);
        if (stepChain == null) {
            throw new RuntimeException(String.format("StepChain with name %s is not exists.", chainName));
        }
        return launch(stepChain,params,key);
    }
}
