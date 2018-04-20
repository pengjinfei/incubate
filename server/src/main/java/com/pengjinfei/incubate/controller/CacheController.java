package com.pengjinfei.incubate.controller;

import com.pengjinfei.incubate.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author PENGJINFEI533
 * @since 2018-04-20
 */
@RestController
public class CacheController {

	@Autowired
	private CacheService cacheService;

	@GetMapping("/one/{id}")
	public String getOne(@PathVariable Integer id) {
		return cacheService.getName(id);
	}

	@GetMapping("/two/{id}")
	public String getTwo(@PathVariable Integer id) {
		return cacheService.getNameByAnotherCache(id);
	}
}
