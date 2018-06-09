package com.pengjinfei.incubate.step.launcher;

import com.pengjinfei.incubate.step.chain.StepChain;
import com.pengjinfei.incubate.step.context.StepContext;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface StepLauncher {

    <T extends Serializable> StepContext launch(StepChain<T> chain, T params, String key) throws Exception;
}
