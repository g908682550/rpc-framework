package com.sankuai.proxy;

import com.sankuai.remoting.dto.RpcMessageChecker;
import com.sankuai.remoting.dto.RpcRequest;
import com.sankuai.remoting.dto.RpcResponse;
import com.sankuai.remoting.transport.ClientTransport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 动态代理类。当动态戴笠对象调用一个方法的时候，实际调用的是下面的invoke方法
 * 正是因为动态代理才让客户端调用的远程方法像是调用本地方法一样
 *
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    /**
     * 用于发送请求给服务端
     */
    private final ClientTransport clientTransport;

    public RpcClientProxy(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoke method:[{}]",method.getName());
        //生成Rpc请求体
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString().replace("-", ""))
                .build();
        RpcResponse rpcResponse=null;
        CompletableFuture<RpcResponse> completableFuture= (CompletableFuture<RpcResponse>)clientTransport.sendRpcRequest(rpcRequest);
        rpcResponse=completableFuture.get();
        RpcMessageChecker.check(rpcResponse,rpcRequest);
        return rpcResponse.getData();
    }
}
