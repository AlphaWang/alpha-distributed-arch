package com.alphawang.distributed.zookeeper.operate;

import com.alphawang.distributed.zookeeper.connect.ZKConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.util.List;

@Slf4j
public class ZkChildrenNoteOperator {

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ZKConnector connector = new ZKConnector();
        
        List<String> strChildList = connector.getZooKeeper().getChildren("/imooc", true);
        log.info("get Children for /imooc: {}", strChildList);
            
        connector.close();    
    }
}
