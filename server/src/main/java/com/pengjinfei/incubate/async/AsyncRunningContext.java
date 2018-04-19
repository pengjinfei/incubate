package com.pengjinfei.incubate.async;

import org.springframework.core.NamedThreadLocal;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
public class AsyncRunningContext {

	private static final ThreadLocal<Boolean> RUN_IN_SCHEDULER =
			new NamedThreadLocal<>("Name of bean running in async scheduler");


	public static Boolean isRunInScheduler() {
		return RUN_IN_SCHEDULER.get() != null;
	}

	static void setRunInScheduler(Boolean bool) {
		if (bool != null) {
			RUN_IN_SCHEDULER.set(bool);
		}
		else {
			RUN_IN_SCHEDULER.remove();
		}
	}

}
