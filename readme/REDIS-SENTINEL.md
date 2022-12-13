# redis-sentinel哨兵集群搭建步骤

## 一、docker搭建redis-sentinel哨兵集群（一主二从三哨兵）

**以下安装步骤基于centos7系统，且提前安装了docker**

1. 准备三台服务器ip1，ip2，ip3，防火墙开放需要用到的端口6379（redis服务器对外提供的访问端口），26379（哨兵用到的端口），为方便演示，本例中将服务器ip1作为主节点，ip2和ip3作为从节点

   ~~~
   firewall-cmd --zone=public --add-port=6379/tcp --permanent
   
   firewall-cmd --zone=public --add-port=26379/tcp --permanent
   
   firewall-cmd --reload   刷新端口信息
   
   firewall-cmd --list-ports  查看放开的端口信息
                                          图一
   ~~~

   

2. 三台服务器各自拉取redis镜像

   ~~~
   docker pull redis
                                          图二
   ~~~

3. 在三台服务器创建对应目录（根据自己情况选择目录创建即可）

   ~~~
   .
   ├── conf
   │   └── redis.conf #存放redis主服务器的配置文件
   └── sentinel #存放redis从属服务器对应的配置文件
       ├── conf
           ├── sentinel.conf
           └── sentinel.log
                                          图三
   ~~~

4. 三台服务器分别从官网下载redis.conf配置文件放到图三对应的目录下

   ~~~
   wget http://download.redis.io/redis-stable/redis.conf
   # 主节点ip1 修改配置文件
   # bind 127.0.0.1 #放开ip地址限制
   protected-mode no #关闭保护模式，这样其他服务器才能访问本服务器
   
   #从节点ip2和ip3 修改配置文件
   # bind 127.0.0.1 #放开ip地址限制
   protected-mode no #关闭保护模式，这样其他服务器才能访问本服务器
   replicaof ip1 6379 #将节点设置为ip1服务器的从属服务器，也可以不设置，在随后启动redis服务时用命令实现
                                           图四
   ~~~

5. 三台服务器在/sentinel/conf目录下创建sentinel.conf哨兵配置文件（也可以从官网下载）

   ~~~
   port 26379
   dir "/data"
   logfile "sentinel.log"
   daemonize no
   sentinel monitor mymaster ip1 6379 2
   #这里是哨兵的关键 mymaster：哨兵集群的名称 配置主节点的ip和端口 ，后面的数字2表示当哨兵集群当中有两个哨兵没有检测到主节点的心跳的时候表明主节点挂掉，开始推选新的主节点。（这里我们的哨兵 集群总共有三个哨兵）
   ~~~

6. 三个节点分别创建并启动redis服务器和哨兵容器（本例中我是把三个哨兵和redis服务器分开创建，也可以在redis服务器中创建哨兵以节约资源）

   ~~~
   # -d 后台启动
   # -v 创建挂载目录，这样以后更新配置文件，只需要重启容器就好，不需要再重新创建容器
   # -e TZ=Asia/Shanghai 指定容器环境信息，此处设置时区信息，不然容器内的时间可能会和服务器时间不一致
   # --name myredis 容器名为myredis
   # docker.io/redis:latest 指定镜像
   # redis-server /etc/redis/redis.conf 以指定的redis.conf启动redis服务器
   docker run -d --privileged=true -p 6379:6379 -v /data/myredis/conf/redis.conf:/etc/redis/redis.conf -e TZ=Asia/Shanghai --name myredis docker.io/redis:latest redis-server /etc/redis/redis.conf
   
   # -d 后台启动
   # -v 创建挂载目录，这样以后更新配置文件，只需要重启容器就好，不需要再重新创建容器
   # -e TZ=Asia/Shanghai 指定容器环境信息，此处设置时区信息，不然容器内的时间可能会和服务器时间不一致
   # --name sentinel 容器名为sentinel
   # docker.io/redis:latest 指定镜像
   # redis-sentinel /data/sentinel.conf 以指定的sentinel.conf启动redis哨兵
   docker run -d --privileged=true --name sentinel -v /data/myredis/sentinel/conf/:/data/ -e TZ=Asia/Shanghai -p 26379:26379 docker.io/redis:latest redis-sentinel /data/sentinel.conf
   
   ~~~

7. 此时哨兵集群已搭建完成，进入容器内查看

   ~~~
   docker exec -it myredis redis-cli # 进入一个从属redis服务器客户端
   #输入info replication命令查看信息
   127.0.0.1:6379> info replication
   # Replication
   role:slave #标识此节点角色为slave从节点
   master_host:ip1 #标识主节点ip地址
   master_port:6379
   master_link_status:up #标识主节点状态为up在线状态
   master_last_io_seconds_ago:1
   master_sync_in_progress:0
   slave_read_repl_offset:548723
   slave_repl_offset:548723
   slave_priority:100
   slave_read_only:1
   replica_announced:1
   connected_slaves:0
   master_failover_state:no-failover
   master_replid:1ff96a5dd5cad5bc8464b80b728755ca150fca2e
   master_replid2:0000000000000000000000000000000000000000
   master_repl_offset:548723
   second_repl_offset:-1
   repl_backlog_active:1
   repl_backlog_size:1048576
   repl_backlog_first_byte_offset:11239
   repl_backlog_histlen:537485
   
   
   docker exec -it myredis redis-cli # 进入主redis服务器客户端
   127.0.0.1:6379> info replication
   # Replication
   role:master #标识此节点为主节点
   connected_slaves:2 #标识该主节点有两个从节点
   slave0:ip=ip1,port=6379,state=online,offset=583453,lag=0 #从节点slave0
   slave1:ip=ip2,port=6379,state=online,offset=583169,lag=1 #从节点slave1
   master_failover_state:no-failover
   master_replid:1ff96a5dd5cad5bc8464b80b728755ca150fca2e
   master_replid2:ac9d07590b764017e517bafa934c1a950b77839e
   master_repl_offset:583453
   second_repl_offset:9796
   repl_backlog_active:1
   repl_backlog_size:1048576
   repl_backlog_first_byte_offset:2347
   repl_backlog_histlen:581107
   
   #任选一个哨兵节点进入查看
   docker exec -it sentinel redis-cli -p 26379
   
   127.0.0.1:26379> info sentinel
   # Sentinel
   sentinel_masters:1
   sentinel_tilt:0
   sentinel_tilt_since_seconds:-1
   sentinel_running_scripts:0
   sentinel_scripts_queue_length:0
   sentinel_simulate_failure_flags:0
   master0:name=mymaster,status=ok,address=ip1:6379,slaves=2,sentinels=4
   # 此处的slaves等信息如果和自己搭建的集群中信息不一致，确定节点是否启动，对应的端口号是否放开
   ~~~

8. 测试主节点挂掉之后，哨兵能否起到重新选举的作用

   ~~~
   docker stop myredis #在ip1服务器，停止主节点redis服务器
   # 进入另外两个从节点查看信息
   127.0.0.1:6379> info replication
   Error: Server closed the connection
   not connected> info replication
   # Replication
   role:master #可以看到之前的从节点已经变成了主节点
   connected_slaves:1 #有一个从节点
   slave0:ip=ip2,port=6379,state=online,offset=671701,lag=1 #从节点信息
   master_failover_state:no-failover
   master_replid:ef52564298b965fb32651878f24b1207bf186e70
   master_replid2:1ff96a5dd5cad5bc8464b80b728755ca150fca2e
   master_repl_offset:671985
   second_repl_offset:662535
   repl_backlog_active:1
   repl_backlog_size:1048576
   repl_backlog_first_byte_offset:11239
   repl_backlog_histlen:660747
   
   # 查看主节点选举日志，在两个从节点服务器ip2，ip3输入如下命令
   docker exec -it myredis /bin/bash # 进入redis容器
   cat redis.log # 查看日志
   # 部分日志信息如下
   1:S 23 Nov 2022 10:48:06.568 # Error condition on socket for SYNC: Connection refused
   1:S 23 Nov 2022 10:48:07.578 * Connecting to MASTER 192.168.133.132:6379
   1:S 23 Nov 2022 10:48:07.578 * MASTER <-> REPLICA sync started
   1:S 23 Nov 2022 10:48:07.578 # Error condition on socket for SYNC: Connection refused
   1:S 23 Nov 2022 10:48:08.586 * Connecting to MASTER 192.168.133.132:6379
   1:S 23 Nov 2022 10:48:08.586 * MASTER <-> REPLICA sync started
   1:S 23 Nov 2022 10:48:08.586 # Error condition on socket for SYNC: Connection refused
   1:S 23 Nov 2022 10:48:09.598 * Connecting to MASTER 192.168.133.132:6379
   1:S 23 Nov 2022 10:48:09.598 * MASTER <-> REPLICA sync started
   1:S 23 Nov 2022 10:48:09.598 # Error condition on socket for SYNC: Connection refused
   1:S 23 Nov 2022 10:48:10.609 * Connecting to MASTER 192.168.133.132:6379 
   #由上面的日志可见从节点在尝试连接原来的主节点，但是都连接失败，因为主节点192.168.133.132已下线
   1:S 23 Nov 2022 10:48:10.609 * MASTER <-> REPLICA sync started
   1:S 23 Nov 2022 10:48:10.609 # Error condition on socket for SYNC: Connection refused
   1:S 23 Nov 2022 10:48:11.576 * Connecting to MASTER 192.168.133.130:6379
   1:S 23 Nov 2022 10:48:11.576 * MASTER <-> REPLICA sync started
   1:S 23 Nov 2022 10:48:11.576 * REPLICAOF 192.168.133.130:6379 enabled (user request from 'id=19 addr=172.17.0.1:39590 laddr=172.17.0.2:6379 fd=10 name=sentinel-2d287dcc-cmd age=2952 idle=0 flags=x db=0 sub=0 psub=0 ssub=0 multi=4 qbuf=345 qbuf-free=20129 argv-mem=4 multi-mem=183 rbs=8192 rbp=5419 obl=45 oll=0 omem=0 tot-mem=29747 events=r cmd=exec user=default redir=-1 resp=2')
   #由上面的日志可见已经选取了新的主节点192.168.133.130，哨兵配置生效
   1:S 23 Nov 2022 10:48:11.578 # Could not create tmp config file (Permission denied)
   1:S 23 Nov 2022 10:48:11.578 # CONFIG REWRITE failed: Permission denied
   1:S 23 Nov 2022 10:48:11.578 * Non blocking connect for SYNC fired the event.
   1:S 23 Nov 2022 10:48:11.579 * Master replied to PING, replication can continue...
   1:S 23 Nov 2022 10:48:11.579 * Trying a partial resynchronization (request 1ff96a5dd5cad5bc8464b80b728755ca150fca2e:662535).
   1:S 23 Nov 2022 10:48:11.579 * Successful partial resynchronization with master.
   1:S 23 Nov 2022 10:48:11.579 # Master replication ID changed to ef52564298b965fb32651878f24b1207bf186e70
   1:S 23 Nov 2022 10:48:11.579 * MASTER <-> REPLICA sync: Master accepted a Partial Resynchronization.
   
   
   
   #查看哨兵选举日志
   docker exec -it sentinel /bin/bash #进入哨兵容器
   
   cat sentinel.log #查看哨兵选举日志
   
   1:X 23 Nov 2022 10:48:12.483 # +switch-master mymaster 192.168.133.132 6379 192.168.133.130 6379
   #切换主节点192.168.133.132 6379  到 192.168.133.130 6379
   1:X 23 Nov 2022 10:48:12.483 * +slave slave 192.168.133.131:6379 192.168.133.131 6379 @ mymaster 192.168.133.130 6379
   #将从节点192.168.133.131 挂到新的主节点上
   1:X 23 Nov 2022 10:48:12.483 * +slave slave 192.168.133.132:6379 192.168.133.132 6379 @ mymaster 192.168.133.130 6379
   #将从节点192.168.133.132 挂到新的主节点上
   1:X 23 Nov 2022 10:48:12.485 * Sentinel new configuration saved on disk
   1:X 23 Nov 2022 10:48:42.495 # +sdown slave 192.168.133.132:6379 192.168.133.132 6379 @ mymaster 192.168.133.130 6379
   #sdown标识新的从节点192.168.133.132（原来的主节点）已经主观下线
   
   
   ~~~



## 二、springboot整合redis-sentinel集群

1. 引入相关依赖

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

   

2. 编写application.yml配置文件

   ~~~yaml
   redis:
       lettuce:
         pool:
           min-idle: 0
           max-idle: 8
           max-active: 8
   
         #redis集群分为主从模式、哨兵模式、集群模式三种
   
         #配置redis集群哨兵模式需要打开下面注释，单机不需要打开
       cluster:
         # 集群信息
         nodes: 192.168.133.130:6379, 192.168.133.131:6379, 192.168.133.132:6379
         # 默认值是5 一般当此值设置过大时，容易报：Too many Cluster redirections
         max-redirects: 5
       #哨兵也要配置，不然会报错
       sentinel:
         master: mymaster
         #配置的是哨兵的ip地址:端口号
         nodes: 192.168.133.130:26379, 192.168.133.131:26379, 192.168.133.132:26379
   ~~~

3. 配置redis序列化

   ~~~java
   import com.fasterxml.jackson.annotation.JsonAutoDetect;
   import com.fasterxml.jackson.annotation.PropertyAccessor;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.data.redis.connection.RedisConnectionFactory;
   import org.springframework.data.redis.core.RedisTemplate;
   import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
   import org.springframework.data.redis.serializer.StringRedisSerializer;
   
   /**
    * 配置redis
    */
   @Configuration
   public class RedisConfig {
   
       @Bean
       @SuppressWarnings("all")
       public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
           RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
           template.setConnectionFactory(factory);
           //json序列化配置
           Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
           ObjectMapper om = new ObjectMapper();
           om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
           om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
           jackson2JsonRedisSerializer.setObjectMapper(om);
           //String的序列化
           StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
           // key采用String的序列化方式
           template.setKeySerializer(stringRedisSerializer);
           // hash的key也采用String的序列化方式
           template.setHashKeySerializer(stringRedisSerializer);
           // value序列化方式采用jackson
           template.setValueSerializer(jackson2JsonRedisSerializer);
           // hash的value序列化方式采用jackson
           template.setHashValueSerializer(jackson2JsonRedisSerializer);
           template.afterPropertiesSet();
   
           return template;
       }
   }
   
   ~~~