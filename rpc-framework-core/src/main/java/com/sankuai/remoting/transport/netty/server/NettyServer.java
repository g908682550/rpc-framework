package com.sankuai.remoting.transport.netty.server;

import com.sankuai.config.CustomShutdownHook;
import com.sankuai.provider.ServiceProvider;
import com.sankuai.provider.ServiceProviderImpl;
import com.sankuai.registry.ServiceRegistry;
import com.sankuai.registry.impl.ZkServiceRegistry;
import com.sankuai.remoting.dto.RpcRequest;
import com.sankuai.remoting.dto.RpcResponse;
import com.sankuai.remoting.transport.netty.codec.kyro.NettyKryoDecoder;
import com.sankuai.remoting.transport.netty.codec.kyro.NettyKryoEncoder;
import com.sankuai.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 服务端。接收客户端消息，并且根据客户端的消息调用响应的方法，然后返回结果给客户端
 */
@Slf4j
public class NettyServer {
    private String host;
    private int port;
    private KryoSerializer kryoSerializer;
    private ServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.kryoSerializer = new KryoSerializer();
        this.serviceRegistry = new ZkServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
    }

    /**
     * 发布服务
     * @param service 服务
     * @param serviceClass 服务类
      * @param <T> 泛型
     */
    public <T> void publishService(T service,Class<T> serviceClass){
        serviceProvider.addServiceProvider(service,serviceClass);
        serviceRegistry.registerService(serviceClass.getCanonicalName(),new InetSocketAddress(host,port));
        start();
    }

    private void start(){
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列等待连接的个数
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(host, port).sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("occur exception when start server:",e);
        }finally {
            log.error("shutdown boosGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
