package com.sankuai.remoting.transport.netty.server;

import com.sankuai.remoting.dto.RpcRequest;
import com.sankuai.remoting.dto.RpcResponse;
import com.sankuai.remoting.handler.RpcRequestHandler;
import com.sankuai.util.enumeration.RpcMessageTypeEnum;
import com.sankuai.util.factory.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义服务端的ChannelHandler来处理客户端发送过来的数据
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try{
            log.info("server receive msg:[{}]",msg);
            RpcRequest rpcRequest=(RpcRequest)msg;
            if(rpcRequest.getRpcMessageTypeEnum()== RpcMessageTypeEnum.HEART_BEAT){
                log.info("receive heat beat msg from client");
                return;
            }
            //执行目标方法
            Object result = rpcRequestHandler.handle(rpcRequest);
            if(ctx.channel().isActive()&&ctx.channel().isWritable()){
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }else{
                log.error("not writable now,message dropped");
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state==IdleState.READER_IDLE){
                ctx.close();
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
