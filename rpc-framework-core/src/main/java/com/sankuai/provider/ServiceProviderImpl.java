package com.sankuai.provider;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider{

    /**
     * 接口名和服务的对应关系
     */
    private static Map<String,Object> serviceMap=new ConcurrentHashMap<>();
    private static Set<String> registeredService=ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service, Class<T> serviceClass) {
        String serviceName=serviceClass.getCanonicalName();
        if(registeredService.contains(serviceName)){
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName,service);
        log.info("Add service：{} and interfaces:{}",serviceName,service.getClass().getInterfaces());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(null == service){
            throw new RuntimeException("找不到服务");
        }
        return service;
    }
}
