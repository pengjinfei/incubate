package com.pengjinfei.incubate.step.launcher;

import com.pengjinfei.incubate.step.StepStatus;
import com.pengjinfei.incubate.step.chain.StepChain;
import com.pengjinfei.incubate.step.context.StepContext;
import com.pengjinfei.incubate.step.context.StepContextDao;
import com.pengjinfei.incubate.step.interceptor.StepInterceptor;
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

    public SimpleStepLauncher(StepContextDao stepContextDao) {
        this.stepContextDao = stepContextDao;
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
}
