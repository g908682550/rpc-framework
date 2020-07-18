package com.sankuai.util.zk;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CuratorUtils {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 5;
    private static final String CONNECT_STRING = "127.0.0.1:2181";
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
    private static Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();
    /**
     * 操作zk的客户端
     */
    private static CuratorFramework zkClient;

    static {
        zkClient=getZkClient();
    }

    private CuratorUtils() {
    }

    /**
     * 创建持久化节点。不同于临时的节点，持久化节点不会因为客户端断开连接而被删除
     * @param path
     */
    public static void createPersistentNode(String path) {
        try{
            if(registeredPathSet.contains(path)||zkClient.checkExists().forPath(path)!=null){
                log.info("节点已经存在，节点为：[{}]",path);
            }else{
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点创建成功，节点为：[{}]",path);
            }
            registeredPathSet.add(path);
        }catch (Exception e){
            throw new RuntimeException("创建节点失败");
        }
    }

    /**
     * 获取某个节点下的所有子节点，也就是获取所有提供服务生产者的地址
     * @param serviceName
     * @return
     */
    public static List<String> getChildrenNodes(String serviceName){
        if(serviceAddressMap.containsKey(serviceName)){
            return serviceAddressMap.get(serviceName);
        }
        List<String> result= Lists.newArrayList();
        String servicePath=ZK_REGISTER_ROOT_PATH+"/"+serviceName;
        try{
            result =zkClient.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName,result);
        }catch (Exception e){
            throw new RuntimeException("未找到该服务");
        }
        return result;
    }

    public static void clearRegistry(){
        registeredPathSet.stream().parallel().forEach(p->{
            try{
                zkClient.delete().forPath(p);
            }catch (Exception e){
                throw new RuntimeException("清空节点异常");
            }
        });
    }

    private static CuratorFramework getZkClient(){
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                //连接服务器地址
                .connectString(CONNECT_STRING)
                //重试策略
                .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }

    private static void registerWatcher(CuratorFramework zkClient,String serviceName){
        String servicePath=ZK_REGISTER_ROOT_PATH+"/"+serviceName;
        PathChildrenCache pathChildrenCache=new PathChildrenCache(zkClient,servicePath,true);
        PathChildrenCacheListener pathChildrenCacheListener=(curatorFramework, pathChildrenCacheEvent) ->{
            List<String> serviceAddresses=curatorFramework.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName,serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try{
            pathChildrenCache.start();
        }catch (Exception e){
            throw new RuntimeException("监听异常");
        }
    }

}
