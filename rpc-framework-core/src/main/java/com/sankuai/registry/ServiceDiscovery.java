package com.sankuai.registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);

}
