package com.pengjinfei.incubate.step.chain;

import com.pengjinfei.incubate.step.*;
import com.pengjinfei.incubate.step.context.StepContext;
import com.pengjinfei.incubate.step.exception.RetryableException;

import java.io.Serializable;

/**
 * Created on 6/9/18
 *
 * @author jinfei
 */
public class StepNode<T extends Serializable> {

    private StepNode<T> next;

    private final Step<T> delegated;

    public StepNode(Step<T> delegated) {
        this.delegated = delegated;
    }

    public void execute(T t, StepContext context) throws RetryableException {
        Exception exception = null;
        if (shouldExecute(context, StepStatus.PREPARE)) {
            try {
                delegated.preCommit(t, context);
            } catch (Exception e) {
                if (e instanceof RetryableException) {
                    throw e;
                } else {
                    exception = e;
                    context.setLastException(e);
                    context.setStatus(StepStatus.ROLLBACK);
                }
            }
        }

        if (next != null && exception == null) {
            try {
                next.execute(t, context);
                context.setStatus(StepStatus.COMMIT);
            } catch (Exception e) {
                if (e instanceof RetryableException) {
                    throw e;
                } else {
                    context.setStatus(StepStatus.ROLLBACK);
                }
            }
        }

        if (delegated instanceof RollbackableStep && (exception != null || shouldExecute(context, StepStatus.ROLLBACK))) {
            RollbackableStep<T> rollbackableStep = (RollbackableStep<T>) delegated;
            try {
                rollbackableStep.rollback(t, context);
            } catch (Exception e) {
                throw new RetryableException(e);
            }
        }
        if (delegated instanceof CommitableStep && exception == null && shouldExecute(context, StepStatus.COMMIT)) {
            CommitableStep<T> commitableStep = (CommitableStep<T>) this.delegated;
            try {
                commitableStep.commit(t, context);
            } catch (Exception e) {
                throw new RetryableException(e);
            }
        }
    }

    private boolean shouldExecute(StepContext context, StepStatus status) {
        if (context.getStatus() != status) {
            return false;
        }
        if (context.isRunning()) {
            return true;
        }
        if (context.getBreakPointStep().equals(delegated.getName())) {
            context.setRunning(true);
            return true;
        }
        return false;
    }

    public StepNode<T> getNext() {
        return next;
    }

    public void setNext(StepNode<T> next) {
        this.next = next;
    }

    public Step<T> getDelegated() {
        return delegated;
    }
}

