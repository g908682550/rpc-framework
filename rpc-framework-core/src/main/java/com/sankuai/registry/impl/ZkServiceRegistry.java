package com.sankuai.registry.impl;

import com.sankuai.registry.ServiceRegistry;
import com.sankuai.util.zk.CuratorUtils;

import java.net.InetSocketAddress;

public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        //根节点下注册子节点：服务
        String servicePath= CuratorUtils.ZK_REGISTER_ROOT_PATH+"/"+serviceName+inetSocketAddress.toString();
        CuratorUtils.createPersistentNode(servicePath);
    }
}
