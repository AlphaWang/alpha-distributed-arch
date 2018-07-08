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
 * 
 * 
 * Clean zk data before execute:
 * 
 * > addauth digest imooc1:123456
 * > rmr /imooc/acl
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
        log.info("1. create Node {} with OPEN_ACL_UNSAFE: {}", "/imooc/acl", ZooDefs.Ids.OPEN_ACL_UNSAFE);
        connector.getZooKeeper().create("/imooc/acl", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        /**
         * 2. 自定义用户认证访问
         *  
         * zkCli (setAcl scheme:id:permissions) 
         * 
         * > setAcl /imooc/acl/digest digest:imooc1:ee8R/pr2P4sGnQYNGyw2M5S5IMU=:cdrwa
         * > addAuth digest imooc1:123456         
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
        String digestPath = "/imooc/acl/digest";
        List<ACL> acls = new ArrayList<ACL>();
        Id imooc1 = new Id("digest", DigestAuthenticationProvider.generateDigest("imooc1:123456"));
        Id imooc2 = new Id("digest", DigestAuthenticationProvider.generateDigest("imooc2:123456"));
        acls.add(new ACL(ZooDefs.Perms.ALL, imooc1));
        acls.add(new ACL(ZooDefs.Perms.READ, imooc2));
        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE, imooc2));

        log.info("2.1 create Node {} with digest ACLs: {}", digestPath, acls);
        connector.getZooKeeper().create(digestPath, "testdigest".getBytes(), acls, CreateMode.PERSISTENT);

        // 注册过的用户必须通过addAuthInfo才能操作节点，参考命令行 addauth
        log.info("2.2 create child Node with digest auth {}.", "imooc1:123456");
        connector.getZooKeeper().addAuthInfo("digest", "imooc1:123456".getBytes());
        connector.getZooKeeper().create(digestPath + "/childtest", "childtest".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
       
        Stat stat = new Stat();
        byte[] data = connector.getZooKeeper().getData(digestPath, false, stat);
        log.info("2.3 get data for {} : {}", digestPath, new String(data));
        connector.getZooKeeper().setData(digestPath, "now".getBytes(), 0);
    }

}
