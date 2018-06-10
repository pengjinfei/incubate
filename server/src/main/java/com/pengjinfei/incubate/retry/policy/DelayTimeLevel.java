package com.pengjinfei.incubate.retry.policy;

import java.util.concurrent.TimeUnit;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
public enum  DelayTimeLevel {
    S_1(new DelayTime(TimeUnit.SECONDS,1)),
    S_5(new DelayTime(TimeUnit.SECONDS, 5)),
    S_10(new DelayTime(TimeUnit.SECONDS,10)),
    S_30(new DelayTime(TimeUnit.SECONDS,30)),
    M_1(new DelayTime(TimeUnit.MINUTES,1)),
    M_2(new DelayTime(TimeUnit.MINUTES,2)),
    M_3(new DelayTime(TimeUnit.MINUTES,3)),
    M_4(new DelayTime(TimeUnit.MINUTES,4)),
    M_5(new DelayTime(TimeUnit.MINUTES,5)),
    M_6(new DelayTime(TimeUnit.MINUTES,6)),
    M_7(new DelayTime(TimeUnit.MINUTES,7)),
    M_8(new DelayTime(TimeUnit.MINUTES,8)),
    M_9(new DelayTime(TimeUnit.MINUTES,9)),
    M_10(new DelayTime(TimeUnit.MINUTES,10)),
    M_20(new DelayTime(TimeUnit.MINUTES,20)),
    M_30(new DelayTime(TimeUnit.MINUTES,30)),
    H_1(new DelayTime(TimeUnit.HOURS,1)),
    H_2(new DelayTime(TimeUnit.HOURS,2));

    private DelayTime delayTime;

    DelayTimeLevel(DelayTime delayTime) {
        this.delayTime = delayTime;
    }

    public DelayTime getDelayTime() {
        return delayTime;
    }

    public int toRabbitLevel() {
        return this.ordinal() + 1;
    }

    public static DelayTimeLevel from(DelayTime delayTime) {
        if (delayTime == null) {
            return null;
        }
        DelayTimeLevel[] levels = DelayTimeLevel.values();
        for (DelayTimeLevel level : levels) {
            DelayTime time = level.getDelayTime();
            if (time.getTimeUnit() == delayTime.getTimeUnit() && time.getTime() == delayTime.getTime()) {
                return level;
            }
        }
        return null;
    }
}
