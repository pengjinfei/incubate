package com.pengjinfei.incubate.lock.impl;

import com.pengjinfei.incubate.lock.Lock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * Created on 4/8/18
 *
 * @author Pengjinfei
 */
@RequiredArgsConstructor
@Slf4j
public class ZkLock implements Lock {

    private final CuratorFramework zkClient;

    @Override
    public void release(String path) {
        try {
            zkClient.delete().guaranteed().forPath(path.startsWith("/") ? path : "/" + path);
        } catch (Exception e) {
            log.error(String.format("release lock %s failed.",path),e);
        }
    }

    @Override
    public boolean tryLock(String path) {
        try {
            zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path.startsWith("/") ? path : "/" + path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
