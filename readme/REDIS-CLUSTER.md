# redis-cluster集群搭建步骤

## 一、docker搭建redis-cluster集群（三主三从）步骤

**以下安装步骤基于centos7系统，且提前安装了docker**

> - 步骤一：准备三台主机ip1、ip2、ip3，每台主机上使用如下命令拉取redis镜像
>
>   ~~~
>   docker pull redis
>   ~~~
>
>   
>
> - 步骤二：放开需要使用的端口6381、6382、16381、16382，命令如下：
>
>   ~~~
>   firewall-cmd --zone=public --add-port=16381-16382/tcp --permanent
>   
>   firewall-cmd --zone=public --add-port=6381-6382/tcp --permanent
>   
>   firewall-cmd --reload   刷新端口信息
>   
>   firewall-cmd --list-ports  查看放开的端口信息
>                                          图一
>   ~~~
>
>   
>
> - 步骤三：在三台主机分别创建对应的文件夹，比如
>
>   ~~~
>   ├── 6381
>   │   ├── conf
>   │   │   └── redis.conf
>   │   └── data
>   │       ├── dump.rdb
>   │       └── nodes-6381.conf
>   ├── 6382
>   │   ├── conf
>   │   │   └── redis.conf
>   │   └── data
>   │       ├── dump.rdb
>   │       └── nodes-6382.conf
>                                       图二
>   ~~~
>
>   
>
> - 步骤四：从官网下载redis.conf配置文件并修改
>
>   ```
>   wget http://download.redis.io/redis-stable/redis.conf
>   修改如下：
>   # bind 127.0.0.1 //放开ip地址限制
>   protected-mode no //关闭保护模式，这样其他服务器才能访问本服务器
>   port 6381  //绑定自定义端口，主节点绑定6381，从节点绑定6382
>   daemonize no //允许redis后台运行
>   pidfile /var/run/redis_6381.pid
>   cluster-enabled yes //开启集群配置
>   cluster-config-file nodes_6381.conf //集群的配置 配置文件首次启动自动生成
>                                       图三
>   ```
>
>   将修改好的配置文件放到对应的服务器对应的文件夹下，具体参考图二
>
> - 步骤五：启动每个服务器的redis主从节点：
>
>   ~~~
>   docker run -d -p 6381:6381 -v /data/myredis/cluster/6381/conf/redis.conf:/etc/redis/redis.conf -v /data/myredis/cluster/6381/data/:/data --privileged=true --net=host --name redis-6381 docker.io/redis:latest redis-server /etc/redis/redis.conf
>   
>   docker run -d -p 6381:6381 -v /data/myredis/cluster/6381/conf/redis.conf:/etc/redis/redis.conf -v /data/myredis/cluster/6381/data/:/data --privileged=true --net=host --name redis-6381 docker.io/redis:latest redis-server /etc/redis/redis.conf
>   ~~~
>
> - 步骤六：连接集群（任选一个redis节点创建集群）
>
>   ~~~
>   docker exec -it redis-6381 /bin/sh
>   redis-cli --cluster create ip1:6381 ip1:6382 ip2:6381 ip2:6382 ip3:6381 ip3:6382 --cluster-replicas 1
>   ~~~
>
> - 步骤七：进入查看集群状态信息
>
>   ~~~
>   1. docker exec -it redis-6381 redis-cli -c -h ip1 -p 6381
>      -c是启用集群模式，要注意这点，否则进入节点获取键值对信息时，比如执行get name（获取键为name的value），可能会报错(error) MOVED 5798 127.0.0.1:6380
>      -h是指定主机ip地址
>      -p指定对应端口号
>   2. cluster info #查看集群信息
>       cluster_state:ok #显示ok则集群搭建成功
>       cluster_slots_assigned:16384
>       cluster_slots_ok:16384
>       cluster_slots_pfail:0
>       cluster_slots_fail:0
>       cluster_known_nodes:6
>       cluster_size:3
>       cluster_current_epoch:6
>       cluster_my_epoch:1
>       cluster_stats_messages_ping_sent:3339
>       cluster_stats_messages_pong_sent:3491
>       cluster_stats_messages_sent:6830
>       cluster_stats_messages_ping_received:3486
>       cluster_stats_messages_pong_received:3339
>       cluster_stats_messages_meet_received:5
>       cluster_stats_messages_received:6830
>       total_cluster_links_buffer_limit_exceeded:0
>   
>   ~~~
>
> - 遇到的问题记录：
>
>   1. 执行redis-cli --cluster create ip1:6381 ip1:6382 ip2:6381 ip2:6382 ip3:6381 ip3:6382 --cluster-replicas 1命令后，一直Waiting for the cluster to join..无反应，这是由于未开启集群总线对应端口造成的，进行如下操作：
>
>      - 中断当前命令的执行（ctrl+c）
>
>      - 删除每个redis节点下的aof、rdb、nodes-6381.conf、nodes-6382.conf文件（如果按照本篇文章步骤进行的话，存放于data目录下）
>
>      - 登录redis节点清空当前数据库
>
>        > ~~~
>        > docker exec -it redis-6381 redis-cli -c -h ip -p 6381
>        > flushdb
>        > ~~~
>        >
>        > 如果不执行上述两个步骤，在重新创建集群时可能报错
>        >
>        > ~~~
>        > Node ip:6381 is not empty. Either the node already knows other
>        > ~~~
>        >
>        > 
>
>      - 关闭所有服务器的redis节点
>
>      - 放开集群总线端口
>
>        > ~~~
>        > firewall-cmd --zone=public --add-port=16381-16382/tcp --permanent
>        > 
>        > firewall-cmd --zone=public --add-port=6381-6382/tcp --permanent
>        > 
>        > firewall-cmd --reload   刷新端口信息
>        > 
>        > firewall-cmd --list-ports  查看放开的端口信息
>        > ~~~
>
>      - 重启所有服务器的redis节点，再次创建集群（转到步骤五）

## 二、springboot整合redis-cluster集群

1. 导入项目依赖

   ~~~xml
   		<dependency>
               <groupId>org.apache.commons</groupId>
               <artifactId>commons-pool2</artifactId>
               <version>2.9.0</version>
           </dependency>
   
           <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-redis -->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-data-redis</artifactId>
               <version>2.4.0</version>
           </dependency>
   
           <!-- 需要引入这个依赖，不然在与redis集群连接时会报错nested exception is io.lettuce.core.RedisConnectionException: Unable to connect to
            版本不兼容的问题造成的-->
           <dependency>
               <groupId>io.lettuce</groupId>
               <artifactId>lettuce-core</artifactId>
               <version>5.3.7.RELEASE</version>
           </dependency>
   ~~~

2. 编写配置文件

   ~~~yaml
   #redis配置
     redis:
       # 单机模式
       # host: 192.168.133.130
       # port: 6379
       lettuce: # springboot2.0之后使用lettuce客户端连接redis服务器，需要在pom文件中引入相应依赖
         pool:
           min-idle: 0
           max-idle: 8
           max-active: 8
   
       #redis集群分为主从模式、哨兵模式、集群模式三种
   
       #配置redis-cluster集群模式打开下面注释
       cluster:
         nodes:
         # 此处的ip1、ip2、ip3需要配置成自己的redis服务器地址
           - ip1:6381
           - ip1:6382
           - ip2:6381
           - ip2:6382
           - ip3:6381
           - ip3:6382
         max-redirects: 5
       timeout: 6000ms
       database: 0
   ~~~

   

## 三、参考

三主三从redis cluster集群模式参考http://t.zoukankan.com/lfl17718347843-p-12332396.html

> 参考了上面这个教程大部分内容，但是发现对于自己的服务器还是有问题（问题在上述已经记录），集群不能成功创建，查阅了其他资料需要放开三主三从服务器对应的端口，比如我搭建集群的端口采用6381、6382，就还需要额外放开6381+10000=16381、6382+10000=16382端口（集群总线端口），具体可参考如下两个链接：
> https://blog.csdn.net/IT_rookie_newbie/article/details/120831949
> https://blog.csdn.net/impressionw/article/details/88616509
>
> centos7 放开端口命令：
>
> firewall-cmd --zone=public --add-port=16381-16382/tcp --permanent
>
> #集群总线
> 每个Redis集群中的节点都需要打开两个TCP连接。一个连接用于正常的给Client提供服务，比如6379，还有一个额外的端口（通过在这个端口号上加10000）作为数据端口，例如：redis的端口为6379，那么另外一个需要开通的端口是：6379 + 10000， 即需要开启 16379。16379端口用于集群总线，这是一个用二进制协议的点对点通信信道。这个集群总线（Cluster bus）用于节点的失败侦测、配置更新、故障转移授权，等等。