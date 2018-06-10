package com.pengjinfei.incubate.retry.context;

import com.pengjinfei.incubate.retry.policy.DelayTime;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
@Data
public class RetryContext implements Serializable {

    private DelayTime lastDelayTime;
    private List<Date> retryTimes;
    private transient Exception lastException;
}
