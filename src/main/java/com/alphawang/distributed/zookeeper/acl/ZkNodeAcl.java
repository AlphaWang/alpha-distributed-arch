package com.alphawang.distributed.zookeeper.acl;

import com.alphawang.distributed.zookeeper.connect.ZKConnector;
import com.alphawang.distributed.zookeeper.connect.ZKWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * zkCli > getAcl path
 */
@Slf4j
public class ZkNodeAcl {

    public static void main(String[] args) throws InterruptedException, KeeperException, NoSuchAlgorithmException {
        ZKConnector connector = new ZKConnector();

        /**
         * 1. acl 任何人都可以访问
         * 
         * zkCli (setAcl scheme:id:permissions) 
         * 
         * > setAcl /imooc/acl world:anyone:cdrwa
         * 
         * 
         * zkCli (getAcl) 
         * 
         * > getAcl /imooc/acl
         *   'world,'anyone
         *   : cdrwa
         */
        connector.getZooKeeper().create("/imooc/acl", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        /**
         * 2. 自定义用户认证访问
         *  
         * zkCli (setAcl scheme:id:permissions) 
         * 
         * > setAcl /imooc/acl/digest digest:imooc1:ee8R/pr2P4sGnQYNGyw2M5S5IMU=:cdrwa
         *          
         *          
         * zkCli (getAcl)  
         * 
         * > getAcl /imooc/acl/digest
         *  'digest,'imooc1:ee8R/pr2P4sGnQYNGyw2M5S5IMU=
         *  : cdrwa
         *   'digest,'imooc2:eBdFG0gQw0YArfEFDCRP3LzIp6k=
         *  : r
         *  'digest,'imooc2:eBdFG0gQw0YArfEFDCRP3LzIp6k=
         *  : cd
         */
       
        List<ACL> acls = new ArrayList<ACL>();
        Id imooc1 = new Id("digest", DigestAuthenticationProvider.generateDigest("imooc1:123456"));
        Id imooc2 = new Id("digest", DigestAuthenticationProvider.generateDigest("imooc2:123456"));
        acls.add(new ACL(ZooDefs.Perms.ALL, imooc1));
        acls.add(new ACL(ZooDefs.Perms.READ, imooc2));
        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE, imooc2));
        connector.getZooKeeper().create("/imooc/acl/digest", "testdigest".getBytes(), acls, CreateMode.EPHEMERAL);
    }

}
