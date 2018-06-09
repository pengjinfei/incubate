package com.pengjinfei.incubate.step.configuration;

import com.pengjinfei.incubate.step.Step;
import com.pengjinfei.incubate.step.chain.StepChainBuilderFactory;
import com.pengjinfei.incubate.step.context.StepContext;
import com.pengjinfei.incubate.step.context.StepContextDao;
import com.pengjinfei.incubate.step.launcher.StepLauncher;
import com.pengjinfei.incubate.step.repository.MapStepRepository;
import com.pengjinfei.incubate.step.repository.StepRepository;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
@Configuration
@Aspect
public class SimpleStepConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private boolean initialized = false;
    private AtomicReference<StepContextDao> stepContextDao = new AtomicReference<>();
    private AtomicReference<StepLauncher> stepLauncher = new AtomicReference<>();

    @Pointcut("within(com.pengjinfei.incubate.step.Step+)")
    public void stepPointcut() {
    }

    @Before("stepPointcut()")
    public void before(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        Object obj = joinPoint.getThis();
        if (args.length == 2 && args[1] instanceof StepContext && obj instanceof Step) {
            StepContext stepContext = (StepContext) args[1];
            stepContext.setBreakPointStep(((Step) obj).getName());
            stepContextDao.get().save(stepContext);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public StepChainBuilderFactory stepChainBuilderFactory() {
        return new StepChainBuilderFactory(stepRepository());
    }

    @Bean
    public StepRepository stepRepository() {
        return new MapStepRepository();
    }

    @Bean
    public StepContextDao stepContextDao() {
        return createLazyProxy(stepContextDao, StepContextDao.class);
    }

    @Bean
    public StepLauncher stepLauncher() {
        return createLazyProxy(stepLauncher, StepLauncher.class);
    }

    private void initialize() throws Exception {
        if (initialized) {
            return;
        }
        Collection<StepConfigurer> configurers = applicationContext.getBeansOfType(StepConfigurer.class).values();
        if (CollectionUtils.isEmpty(configurers) || configurers.size() != 1) {
            throw new BeanInitializationException("There must be one stepConfigurers");
        }
        StepConfigurer stepConfigurer = configurers.iterator().next();
        stepContextDao.set(stepConfigurer.getStepContextDao());
        stepLauncher.set(stepConfigurer.getsStepLauncher());
        initialized = true;
    }

    private <T> T createLazyProxy(AtomicReference<T> reference, Class<T> type) {
        ProxyFactory factory = new ProxyFactory();
        factory.setTargetSource(new ReferenceTargetSource<T>(reference));
        factory.addAdvice(new PassthruAdvice());
        factory.setInterfaces(type);
        @SuppressWarnings("unchecked")
        T proxy = (T) factory.getProxy();
        return proxy;
    }

    private class PassthruAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return invocation.proceed();
        }

    }

    private class ReferenceTargetSource<T> extends AbstractLazyCreationTargetSource {

        private AtomicReference<T> reference;

        ReferenceTargetSource(AtomicReference<T> reference) {
            this.reference = reference;
        }

        @Override
        protected Object createObject() throws Exception {
            initialize();
            return reference.get();
        }
    }
}
