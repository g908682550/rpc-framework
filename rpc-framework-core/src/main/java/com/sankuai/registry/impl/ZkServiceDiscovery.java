package com.sankuai.registry.impl;

import com.sankuai.loadbalance.LoadBalance;
import com.sankuai.loadbalance.RandomLoadBalance;
import com.sankuai.registry.ServiceDiscovery;
import com.sankuai.util.zk.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        //默认使用随机算法返回一个服务器地址
        this(new RandomLoadBalance());
    }

    public ZkServiceDiscovery(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<String> serviceUrlList= CuratorUtils.getChildrenNodes(serviceName);
        //根据算法从服务器列表中选择一个地址
        String serivceAddress = loadBalance.selectSerivceAddress(serviceUrlList);
        //切分主机和端口号
        String[] socketAddressArray = serivceAddress.split(":");
        String host=socketAddressArray[0],port=socketAddressArray[1];
        return new InetSocketAddress(host, Integer.parseInt(port));
    }
}
