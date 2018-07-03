package com.alphawang.distributed.zookeeper.operate;

import com.alphawang.distributed.zookeeper.callback.ZkDeleteCallback;
import com.alphawang.distributed.zookeeper.callback.ZkStatCallback;
import com.alphawang.distributed.zookeeper.connect.ZKWatcher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

@Slf4j
@Data
public class ZKNodeOperator {
    
    private ZooKeeper zooKeeper;
    
    private static final String zkServerPath = "127.0.0.1:2181";
    private static final int timeout = 50000000;
    
    public ZKNodeOperator(String server) {
        try {
            zooKeeper = new ZooKeeper(server, timeout, new ZKWatcher());
        } catch (IOException e) {
            log.error("Failed to connect zk server.", e);
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    log.error("Failed to close zk connection.", e);
                }
            }
        }
    }

    /**
     * acl：控制权限策略
     *      Ids.OPEN_ACL_UNSAFE --> world:anyone:cdrwa
     *      CREATOR_ALL_ACL --> auth:user:password:cdrwa
     * 
     * createMode：节点类型, 是一个枚举
     * 		PERSISTENT：持久节点
     * 		PERSISTENT_SEQUENTIAL：持久顺序节点
     * 		EPHEMERAL：临时节点
     * 		EPHEMERAL_SEQUENTIAL：临时顺序节点
     * @param path
     * @param data
     * @param acls
     */
    public void createZKNode(String path, byte[] data, List<ACL> acls) {
        try {
            String result = zooKeeper.create(path, data, acls, CreateMode.PERSISTENT);
            log.info("Created node: {}, result: {}", path, result);
//            Thread.sleep(2000);
        } catch (KeeperException | InterruptedException e) {
            log.error("Failed to create node: {}", path, e);
        }
    }
    
    public void setZKNode(String path, byte[] data, int version) {
        try {
            Stat stat = zooKeeper.setData(path, data, version);
            log.info("Set node: {}; result: {}", path, stat);
//            Thread.sleep(2000);
        } catch (KeeperException | InterruptedException e) {
            log.error("Failed to set node: {}", path, e);
        }
    }

    public void setZKNodeAsync(String path, byte[] data, int version) {
//        try {
            zooKeeper.setData(path, data, version, new ZkStatCallback(), "Test Ctx");
            log.info("Set node async: {}.", path);
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            log.error("Failed to set node async: {}", path, e);
//        }
    }
    
    public void deleteZKNode(String path, int version) {
        try {
            zooKeeper.delete(path, version);
            log.info("Delete node: {} {}", path, version);
//            Thread.sleep(2000);
        } catch (KeeperException | InterruptedException e) {
            log.error("Failed to delete node: {}", path, e);
        }
    }

    /**
     * 推荐使用回调方式删除，否则得不到通知。
     * @param path
     * @param version
     */
    public void deleteZKNodeAsync(String path, int version) {
//        try {
            zooKeeper.delete(path, version, new ZkDeleteCallback(), "TEST Ctx delete");
            log.info("Delete node async: {} {}", path, version);
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            log.error("Failed to delete node async: {}", path, e);
//        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZKNodeOperator operator = new ZKNodeOperator(zkServerPath);
        
        operator.createZKNode("/testnode", "testnode-data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        operator.setZKNode("/testnode", "testnode-data-2".getBytes(), 0);
        operator.setZKNode("/testnode", "testnode-data-3".getBytes(), 0);
        operator.setZKNodeAsync("/testnode", "testnode-data-4".getBytes(), 1);

        operator.createZKNode("/testdelete", "testnode-data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        operator.deleteZKNode("/testdelete", 1);
        operator.deleteZKNode("/testdelete", 0);

        operator.createZKNode("/testdelete2", "testnode-data2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        operator.deleteZKNodeAsync("/testdelete2", 1);
        operator.deleteZKNodeAsync("/testdelete2", 0);
    }
    
}
