package com.alphawang.distributed.zookeeper.callback;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

@Slf4j
public class ZkStatCallback implements AsyncCallback.StatCallback {
    @Override 
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        log.warn("[StatCallback] >>>> rc={}, path={}, ctx={}, stat={}", rc, path, ctx, stat);
    }
}
