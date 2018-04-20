package com.pengjinfei.incubate.service;

import com.pengjinfei.incubate.async.StatefulAsync;
import com.pengjinfei.incubate.lock.Locked;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
@Service
@Slf4j
public class AsyncService {

	//测试执行链顺序
	@StatefulAsync
	@Locked
	@Transactional
	public void testAsync() {
		log.info("i am running");
	}
}
