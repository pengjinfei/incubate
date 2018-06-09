package com.pengjinfei.incubate.step.configuration;

import com.pengjinfei.incubate.step.context.StepContextDao;
import com.pengjinfei.incubate.step.launcher.StepLauncher;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public interface StepConfigurer {

    StepContextDao getStepContextDao();

    StepLauncher getsStepLauncher();
}
