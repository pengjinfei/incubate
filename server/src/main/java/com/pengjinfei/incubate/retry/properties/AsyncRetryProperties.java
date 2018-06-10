package com.pengjinfei.incubate.retry.properties;

import com.pengjinfei.incubate.retry.callback.AsyncRetryCallback;
import com.pengjinfei.incubate.retry.policy.RetryPolicy;
import lombok.Data;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;

import java.util.List;

/**
 * Created on 6/10/18
 *
 * @author jinfei
 */
@Data
public class AsyncRetryProperties {
    private List<RollbackRuleAttribute> retryRules;
    private RetryPolicy retryPolicy;
    private AsyncRetryCallback callback;
}
