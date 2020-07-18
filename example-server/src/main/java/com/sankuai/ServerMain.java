package com.sankuai;

import com.sankuai.remoting.transport.netty.server.NettyServer;

public class ServerMain {

    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        NettyServer nettyServer=new NettyServer("127.0.0.1",8888);
        nettyServer.publishService(helloService,HelloService.class);
    }
}
