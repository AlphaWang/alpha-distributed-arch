package com.alphawang.distributed.zookeeper.operate;

import com.alphawang.distributed.zookeeper.connect.ZKConnector;
import com.alphawang.distributed.zookeeper.connect.ZKWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.List;

@Slf4j
public class ZkNodeExists {

    public static void main(String[] args) throws InterruptedException, KeeperException {
        ZKConnector connector = new ZKConnector();

        Stat stat = connector.getZooKeeper().exists("/imooc", new ZKWatcher());
        if (stat == null) {
            log.info("Node not exists.");
        } else {
            log.info("Node exists. {}; version={}", stat, stat.getVersion());
        }
        
        Thread.sleep(10000);
    }

}
