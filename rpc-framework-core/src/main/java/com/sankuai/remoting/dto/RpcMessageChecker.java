package com.sankuai.remoting.dto;


import com.sankuai.util.enumeration.RpcErrorMessageEnum;
import com.sankuai.util.enumeration.RpcResponseCode;
import com.sankuai.util.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RpcMessageChecker {
    private static final String INTERFACE_NAME = "interfaceName";

    private RpcMessageChecker() {
    }

    public static void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
