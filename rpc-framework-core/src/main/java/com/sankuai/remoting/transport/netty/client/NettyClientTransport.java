package com.sankuai.remoting.transport.netty.client;

import com.sankuai.registry.ServiceDiscovery;
import com.sankuai.registry.impl.ZkServiceDiscovery;
import com.sankuai.remoting.dto.RpcRequest;
import com.sankuai.remoting.dto.RpcResponse;
import com.sankuai.remoting.transport.ClientTransport;
import com.sankuai.util.factory.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyClientTransport implements ClientTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClientTransport(){
        this.serviceDiscovery=new ZkServiceDiscovery();
        this.unprocessedRequests= SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse> resultFuture=new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        Channel channel = ChannelProvider.get(inetSocketAddress);
        if(channel!=null&&channel.isActive()){
            //放入未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(),resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future->{
               if(future.isSuccess()){
                   log.info("client send message:[{}]",rpcRequest);
               }else{
                   future.channel().close();
                   resultFuture.completeExceptionally(future.cause());
               }
            });
        }else{
            throw new IllegalStateException();
        }
        return resultFuture;
    }
}
