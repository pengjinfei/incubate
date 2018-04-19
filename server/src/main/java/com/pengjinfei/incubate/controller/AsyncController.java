package com.pengjinfei.incubate.controller;

import com.pengjinfei.incubate.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author PENGJINFEI533
 * @since 2018-04-19
 */
@RestController
public class AsyncController {

	@Autowired
	private AsyncService asyncService;

	@RequestMapping("/async")
	public void teLock() {
		asyncService.testAsync();
	}
}
