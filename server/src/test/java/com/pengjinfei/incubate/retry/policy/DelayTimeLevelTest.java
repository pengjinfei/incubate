package com.pengjinfei.incubate.retry.policy;

import org.junit.Assert;
import org.junit.Test;

public class DelayTimeLevelTest {

    @Test
    public void testTimeleve() {
        DelayTimeLevel s0 = DelayTimeLevel.S_1;
        Assert.assertEquals(s0.toRabbitLevel(),1);
    }

}