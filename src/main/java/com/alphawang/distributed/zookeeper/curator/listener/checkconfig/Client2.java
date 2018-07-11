package com.alphawang.distributed.zookeeper.curator.listener.checkconfig;

public class Client2 extends AbstractClient {

    public static void main(String[] args) throws Exception {
        new Client2().listen();
    }
    
    @Override 
    protected String getClientName() {
        return "client-2";
    }
}
