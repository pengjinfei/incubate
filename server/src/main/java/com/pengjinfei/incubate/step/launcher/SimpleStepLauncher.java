package com.pengjinfei.incubate.step.launcher;

import com.pengjinfei.incubate.retry.AsyncRetryMeta;
import com.pengjinfei.incubate.retry.operations.AsyncRetryOperations;
import com.pengjinfei.incubate.step.StepStatus;
import com.pengjinfei.incubate.step.chain.StepChain;
import com.pengjinfei.incubate.step.context.StepContext;
import com.pengjinfei.incubate.step.context.StepContextDao;
import com.pengjinfei.incubate.step.interceptor.StepInterceptor;
import com.pengjinfei.incubate.step.repository.StepRepository;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class SimpleStepLauncher implements StepLauncher,BeanNameAware {

    private final StepContextDao stepContextDao;

    private final StepRepository stepRepository;

    private final AsyncRetryOperations asyncRetryOperations;

    public SimpleStepLauncher(StepContextDao stepContextDao, StepRepository stepRepository, AsyncRetryOperations asyncRetryOperations) {
        this.stepContextDao = stepContextDao;
        this.stepRepository = stepRepository;
        this.asyncRetryOperations = asyncRetryOperations;
    }

    @Override
    public <T extends Serializable> StepContext launch(StepChain<T> chain, T params) throws Exception{
        String key = chain.getKeyGenerator().generateKey(params);
        StepContext lastContext = stepContextDao.select(key, chain.getName());
        if (lastContext != null) {
            lastContext.setRunning(false);
        } else {
            lastContext = new StepContext();
            lastContext.setRunning(true);
        }
        lastContext.setStatus(StepStatus.PREPARE);
        lastContext.setStepChain(chain);
        lastContext.setKey(key);
        AsyncRetryMeta meta = new AsyncRetryMeta(beanName, "launch", new Object[]{chain.getName(), params}, key);
        asyncRetryOperations.asyncRun(meta);
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
    public <T extends Serializable> StepContext launch(String chainName, T params) throws Exception {
        StepChain stepChain = stepRepository.getStepChain(chainName);
        if (stepChain == null) {
            throw new RuntimeException(String.format("StepChain with name %s is not exists.", chainName));
        }
        return launch(stepChain,params);
    }

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
