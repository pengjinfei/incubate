package com.pengjinfei.incubate.retry.context;

import com.pengjinfei.incubate.retry.AsyncRetryMeta;
import com.pengjinfei.incubate.retry.policy.DelayTime;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
@Data
public class RetryContext implements Serializable {

    private DelayTime lastDelayTime;
    private List<Date> retryTimes = new LinkedList<>();
    private transient Exception lastException;
    private transient AsyncRetryMeta meta;
}
