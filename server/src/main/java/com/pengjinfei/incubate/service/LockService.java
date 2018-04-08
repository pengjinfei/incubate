package com.pengjinfei.incubate.service;

import com.pengjinfei.incubate.lock.Locked;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created on 4/8/18
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class LockService {

    @Locked
    public void testLock() {
        log.info("i get lock");
    }
}
