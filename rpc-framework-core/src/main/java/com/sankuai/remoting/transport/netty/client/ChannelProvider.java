package com.sankuai.remoting.transport.netty.client;

import com.sankuai.util.factory.SingletonFactory;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于获取Channel对象
 *
 */
public class ChannelProvider {

    private static Map<String, Channel> channels=new ConcurrentHashMap<>();
    private static NettyClient nettyClient;

    static{
        nettyClient= SingletonFactory.getInstance(NettyClient.class);
    }

    private ChannelProvider(){

    }

    public static Channel get(InetSocketAddress inetSocketAddress){
        String key=inetSocketAddress.toString();
        if(channels.containsKey(key)){
            Channel channel = channels.get(key);
            if(channel!=null&&channel.isActive()){
                return channel;
            }else{
                channels.remove(key);
            }
        }
        //重新连接获取channel
        Channel channel=nettyClient.doConnect(inetSocketAddress);
        channels.put(key,channel);
        return channel;
    }

    public static void remove(InetSocketAddress inetSocketAddress){
        String key=inetSocketAddress.toString();
        channels.remove(key);
    }

}
