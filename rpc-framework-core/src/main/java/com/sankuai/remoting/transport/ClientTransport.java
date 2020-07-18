package com.sankuai.remoting.transport;

import com.sankuai.remoting.dto.RpcRequest;

/**
 * 传输RpcRequest
 */
public interface ClientTransport {

    /**
     * 发送消息到服务端
     * @param rpcRequest 客户端发送的消息体
     * @return 服务端返回的数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

}
