package com.sankuai.util.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolFactoryUtils {

    private static Map<String,ExecutorService> threadPools=new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtils(){

    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){
        return createCustomThreadPoolIfAbsent(threadNamePrefix,new CustomThreadPollConfig());
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix,CustomThreadPollConfig customThreadPollConfig){
        return createCustomThreadPoolIfAbsent(threadNamePrefix,customThreadPollConfig,false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix,CustomThreadPollConfig customThreadPollConfig,Boolean daemon){
        ExecutorService threadPool = threadPools.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPollConfig, threadNamePrefix, daemon));
        if(threadPool.isShutdown()||threadPool.isTerminated()){
            threadPools.remove(threadNamePrefix);
            threadPool=createThreadPool(customThreadPollConfig,threadNamePrefix,daemon);
            threadPools.put(threadNamePrefix,threadPool);
        }
        return threadPool;
    }

    private static ExecutorService createThreadPool(CustomThreadPollConfig customThreadPollConfig,String threadNamePrefix,Boolean daemon){
        ThreadFactory threadFactory=createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPollConfig.getCorePoolSize(),customThreadPollConfig.getMaximumPoolSize(),
                customThreadPollConfig.getKeepAliveTime(),
                customThreadPollConfig.getUnit(),
                customThreadPollConfig.getWorkQueue(),
                threadFactory);
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix,Boolean daemon){
        if(threadNamePrefix!=null){
            if(daemon!=null){
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix+"-%d")
                        .setDaemon(daemon).build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix+"-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAllThreadPool(){
        threadPools.entrySet().parallelStream().forEach(entry->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            try{
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            }catch (InterruptedException e){
                executorService.shutdown();
            }
        });
    }
}
