package com.sankuai;

import com.sankuai.dto.HelloParam;
import com.sankuai.proxy.RpcClientProxy;
import com.sankuai.remoting.transport.ClientTransport;
import com.sankuai.remoting.transport.netty.client.NettyClientTransport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMain {

    public static void main(String[] args) {
        ClientTransport rpcClient=new NettyClientTransport();
        RpcClientProxy rpcClientProxy=new RpcClientProxy(rpcClient);
        HelloService helloService=rpcClientProxy.getProxy(HelloService.class);
        for(int i=0;i<100;i++){
            String hello = helloService.hello(new HelloParam("123","234"));
            System.out.println(hello);
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                log.error("thread sleep error");
            }
        }
    }

}
