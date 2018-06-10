package com.pengjinfei.incubate.retry;

public class AsyncRetryMeta {
    private final String beanName;
    private final String methodName;
    private final Object[] args;
    private final String key;

    public AsyncRetryMeta(String beanName, String methodName, Object[] args,String key) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.args = args;
        this.key = key;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getKey() {
        return key;
    }
}
