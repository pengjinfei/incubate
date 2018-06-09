package com.pengjinfei.incubate.step.context;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface StepContextDao {

    void save(StepContext context);

    StepContext select(String key);
}
