package com.pengjinfei.incubate.controller;

import com.pengjinfei.incubate.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 4/8/18
 *
 * @author Pengjinfei
 */
@RestController
public class LockController {

    @Autowired
    private LockService lockService;

    @RequestMapping("/lock")
    public void teLock() {
        lockService.testLock();
    }
}
