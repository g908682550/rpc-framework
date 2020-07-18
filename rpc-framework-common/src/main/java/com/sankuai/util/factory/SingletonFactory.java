package com.sankuai.util.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取单例对象的工厂类
 */
public final class SingletonFactory {

    private static Map<String,Object> objectMap=new HashMap<>();

    private SingletonFactory(){

    }

    public static <T> T getInstance(Class<T> c){
        String key=c.toString();
        Object instance=objectMap.get(key);
        synchronized (c){
            if(instance==null){
                try{
                    instance=c.newInstance();
                    objectMap.put(key,instance);
                }catch(Exception e){
                    throw new RuntimeException(e.getMessage(),e);
                }
            }
        }
        return c.cast(instance);
    }

}
