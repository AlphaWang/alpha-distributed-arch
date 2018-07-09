package com.alphawang.distributed.zookeeper.curator.connect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;

import static com.alphawang.distributed.zookeeper.Constants.LOCAL_ZK_PATH;

@Slf4j
public class CuratorConnector {
    
    @Getter
    private CuratorFramework curatorFramework;
    
    public CuratorConnector() {
        RetryPolicy retryPolicy = getRetryPolicy();
        
        
        curatorFramework = CuratorFrameworkFactory.builder()
            .connectString(LOCAL_ZK_PATH)
            .sessionTimeoutMs(10000)
            .retryPolicy(retryPolicy)
            .namespace("curator_namespace")
            .build();
        
        curatorFramework.start();
    }
    
    private static RetryPolicy getRetryPolicy() {
        /**
         * @param baseSleepTimeMs initial amount of time to wait between retries
         * @param maxRetries max number of times to retry
         * @param maxSleepMs max time in ms to sleep on each retry
         */ 
        // return new ExponentialBackoffRetry(1000, 5);

        /**
         * Retry policy that retries a max number of times
         */
        return new RetryNTimes(3, 5000);

        /**
         * new RetryOneTime(5000) == new RetryNTimes(1, 5000) 
         */
        // return new RetryOneTime(5000);
    }
    
}
