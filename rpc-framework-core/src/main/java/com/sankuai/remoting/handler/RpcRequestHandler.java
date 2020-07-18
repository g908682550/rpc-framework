package com.sankuai.remoting.handler;

import com.sankuai.provider.ServiceProvider;
import com.sankuai.provider.ServiceProviderImpl;
import com.sankuai.remoting.dto.RpcRequest;
import com.sankuai.remoting.dto.RpcResponse;
import com.sankuai.util.enumeration.RpcResponseCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    private static ServiceProvider serverProvider=new ServiceProviderImpl();

    public Object handle(RpcRequest rpcRequest){
        Object service = serverProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest,service);
    }

    /**
     * 根据rpcRequest和service对象特定的方法并返回结果
     * @param rpcRequest
     * @param service
     * @return
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service){
        Object result;
        try{
            Method method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            if(null==method){
                return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
            }
            result= method.invoke(service, rpcRequest.getParameters());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return result;
    }

}
