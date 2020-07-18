package com.sankuai.config;

import com.sankuai.util.concurrent.ThreadPoolFactoryUtils;
import com.sankuai.util.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 当服务端（provider）关闭的时候做一些事情比如取消注册所有服务
 *
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK=new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook(){
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll(){
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            CuratorUtils.clearRegistry();
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }

}
