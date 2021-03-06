package com.sankuai.remoting.transport.netty.client;

import com.sankuai.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的请求
 */
public class UnprocessedRequests {
    private static Map<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures=new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future){
        unprocessedResponseFutures.put(requestId,future);
    }

    public void complete(RpcResponse rpcResponse){
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.get(rpcResponse.getRequestId());
        if(null!=future){
            future.complete(rpcResponse);
        }else{
            throw new IllegalStateException();
        }
    }

}
