package com.alphawang.distributed.zookeeper.curator.operate;

import com.alphawang.distributed.zookeeper.curator.connect.CuratorConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

@Slf4j
public class CuratorOperator {

    public static void main(String[] args) throws Exception {
        CuratorConnector curatorConnector = new CuratorConnector();
        boolean isZkCuratorStarted = curatorConnector.getCuratorFramework().isStarted();
    
        log.info("Curator Started? {} ", isZkCuratorStarted);
        
        String nodePath = "/imooc/curator";
        String data = "curator-data";
        log.info("---- 1. creating node {}", nodePath);
        curatorConnector.getCuratorFramework()
            .create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.PERSISTENT)
            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
            .forPath(nodePath, data.getBytes());
        
        data = "curator-data2";
        log.info("---- 2. setting data {} = {}", nodePath, data);
        
        curatorConnector.getCuratorFramework()
            .setData()
            .withVersion(0)
            .forPath(nodePath, data.getBytes());
        
        log.info("---- 3. deleting node {}", nodePath);
        
        curatorConnector.getCuratorFramework()
            .delete()
            .guaranteed()
            .deletingChildrenIfNeeded()
            .withVersion(0)
            .forPath(nodePath);
    }
}
