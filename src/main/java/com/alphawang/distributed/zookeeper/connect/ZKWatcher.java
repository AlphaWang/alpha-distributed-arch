package com.alphawang.distributed.zookeeper.connect;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

@Slf4j
public class ZKWatcher implements Watcher {
    
    @Override 
    public void process(WatchedEvent event) {
        log.warn("[Watcher] >>> accepted event {}", event);
    }
}
