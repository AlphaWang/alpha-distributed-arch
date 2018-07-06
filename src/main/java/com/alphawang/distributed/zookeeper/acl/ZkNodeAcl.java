package com.alphawang.distributed.zookeeper.acl;

import com.alphawang.distributed.zookeeper.connect.ZKConnector;
import com.alphawang.distributed.zookeeper.connect.ZKWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * zkCli : getAcl path
 */
@Slf4j
public class ZkNodeAcl {

    public static void main(String[] args) throws InterruptedException, KeeperException {
        ZKConnector connector = new ZKConnector();

        // acl 任何人都可以访问
        connector.getZooKeeper().create("/aclimooc", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

}
