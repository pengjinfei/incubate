package com.pengjinfei.incubate.retry.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
@RequiredArgsConstructor
@Getter
public class DelayTime {

    private final TimeUnit timeUnit;

    private final long time;
}
