RPC框架的简易实现
rpc-framework-core为其核心功能，提供网络传输模块以及序列化方式等的具体实现，客户端与服务端Netty的启动类等。
rpc-framework-common提供一些工具（线程池、Zk客户端工具Curator）、异常类、常量类等。
hello-service-api为公共接口
example-server为服务端，服务端发布公共接口的实现类到Zookeeper上
example-client为客户端，通过Zookeeper发现该接口实现类的服务端地址，通过Netty进行网络传输请求服务端进行服务