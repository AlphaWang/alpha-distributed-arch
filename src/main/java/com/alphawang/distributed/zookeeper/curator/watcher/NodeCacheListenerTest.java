package com.alphawang.distributed.zookeeper.curator.watcher;

import com.alphawang.distributed.zookeeper.curator.connect.CuratorConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

@Slf4j
public class NodeCacheListenerTest {

    public static void main(String[] args) {
        CuratorConnector curatorConnector = new CuratorConnector();
        String nodePath = "/imooc/curator";

        /**
         * NodeCache: 监听数据节点的变更，会触发事件
         */
        NodeCache nodeCache = new NodeCache(curatorConnector.getCuratorFramework(), nodePath);
        try {
            nodeCache.start(); // NodeCache 参数: 监听数据节点的变更，会触发事件
        } catch (Exception e) {
            log.error("Failed to start node cache", e);
        }

        getCurrentData(nodeCache);
        
        
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override 
            public void nodeChanged() throws Exception {
                getCurrentData(nodeCache); 
            }
        });

    }
    
    private static void getCurrentData(NodeCache nodeCache) {
        ChildData currentData = nodeCache.getCurrentData();
        if (currentData != null) {
            log.warn("NodeCache current data: {} = {}", currentData.getPath(), new String(currentData.getData()));
        } else {
            log.warn("NodeCache no init data.");
        }
    }
}
