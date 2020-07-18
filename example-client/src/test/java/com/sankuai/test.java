package com.sankuai;

import com.sankuai.remoting.dto.RpcRequest;
import com.sankuai.serialize.kryo.KryoSerializer;
import org.junit.Test;

import java.util.UUID;

public class test {

    @Test
    public void test(){
        RpcRequest request = RpcRequest.builder().requestId(UUID.randomUUID().toString()).methodName("aaaa").build();
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] serialize = kryoSerializer.serialize(request);
        RpcRequest rpcRequest=(RpcRequest) kryoSerializer.deserialize(serialize,RpcRequest.class);
        System.out.println(rpcRequest.getRequestId());
    }
}
