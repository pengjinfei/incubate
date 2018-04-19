package com.pengjinfei.incubate.service;

import com.pengjinfei.incubate.async.StatefulAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
@Service
@Slf4j
public class AsyncService {

	@StatefulAsync
	public void testAsync() {
		log.info("i am running");
	}
}
