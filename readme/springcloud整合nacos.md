# springcloud整合nacos

## 一、前期准备工作

安装好nacos注册配置中心，有一个springcloud项目

版本介绍：

我使用的nacos版本为2.1.2

springboot版本为2.3.0.RELEASE

## 二、整合nacos

1. 导入相关依赖

   ~~~xml
          <!--配置中心 如果你只想使用nacos的配置中心功能，只引入此依赖包就可以-->
           <dependency>
               <groupId>com.alibaba.cloud</groupId>
               <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
               <version>2.2.9.RELEASE</version>
           </dependency>
           <!--注册中心 如果你想使用nacos的注册中心，服务发现功能，还需要引入此依赖包-->
           <dependency>
               <groupId>com.alibaba.cloud</groupId>
               <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
               <version>2.2.9.RELEASE</version>
           </dependency>
   ~~~

2. 编写配置文件

   由于nacos读取的配置文件是bootstrap.yml文件，而不是application.yml文件，所以你需要新建bootstrap.yml文件，在里面编写相关配置，以下配置以服务提供者举例，消费者类似，该配置文件同时配置了配置中心和服务发现（注册中心）的相关信息，也就是使用nacos的配置中心和服务发现功能

   ~~~yml
   spring:
     application:
       name: service-provider  #服务提供者 服务名，也是注册的唯一标识，这个需要配置，它是dataid的构成部分
     cloud:
       nacos:
         # 配置中心
         config:
           server-addr: 192.168.133.130:8848  # 配置nacos server的地址
         # 配置服务提供者
         discovery:
           server-addr: 192.168.133.130:8848  # 配置nacos server的地址
   
   ~~~

3. 在启动类上加上@EnableDiscoveryClient注解，让nacos注册中心可以发现服务，经测试其实不加这个注解也可以被注册中心发现，但还是加一下吧，可读性更高一点

   ~~~java
   @SpringBootApplication
   @MapperScan("com.example.demo2.mapper")
   @EnableDiscoveryClient//通过 Spring Cloud 原生注解 @EnableDiscoveryClient 开启服务注册发现功能，经测试不加这个注解也可以被nacos注册中心发现
   public class Demo2Application {
   
       public static void main(String[] args) {
           SpringApplication.run(Demo2Application.class, args);
       }
   
   }
   ~~~

4. 做好以上配置之后，启动springcloud应用，同时访问nacos注册中心服务，会发现服务管理--服务列表中已经出现你配置的服务信息，如下图，其中service-provider就是你在bootstrap.yml配置文件中配置的服务名
   ![image-20221213134302175](C:\Users\Y05926\AppData\Roaming\Typora\typora-user-images\image-20221213134302175.png)

5. 以上是使用nacos的服务注册发现功能，接下来测试nacos配置中心的自动刷新功能

   在分布式微服务环境下，我们希望有一个统一的配置中心来管理公共的配置信息，这样每次只需要更新配置中心的配置信息，然后通知到注册在配置中心上的各个微服务，动态更新配置就好。而不是每一个微服务都自己维护一套配置信息，每次都去单独维护每个微服务的配置信息，显得比较繁琐，而且容易漏掉。nacos的配置中心提供了这样一个解决方案。如果需要使用nacos的配置中心功能，需要进行上述的配置中心配置

   

   补充：nacos配置中心的配置列表如下，每一个配置都分属于一个dataid和group，dataid的构成如下

   ```plain
   ${prefix}-${spring.profiles.active}.${file-extension}
   ```

   - `prefix` 默认为 `spring.application.name` 的值，也可以通过配置项 `spring.cloud.nacos.config.prefix`来配置。
   - `spring.profiles.active` 即为当前环境对应的 profile，详情可以参考 [Spring Boot文档](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-profiles)。 **注意：当 `spring.profiles.active` 为空时，对应的连接符 `-` 也将不存在，dataId 的拼接格式变成 `${prefix}.${file-extension}`**
   - `file-exetension` 为配置内容的数据格式，可以通过配置项 `spring.cloud.nacos.config.file-extension` 来配置。目前只支持 `properties` 和 `yaml` 类型。

   ![image-20221213135033240](C:\Users\Y05926\AppData\Roaming\Typora\typora-user-images\image-20221213135033240.png)

   点击加号，可以新增配置信息，如图

   ![image-20221213141026438](C:\Users\Y05926\AppData\Roaming\Typora\typora-user-images\image-20221213141026438.png)

   填写dataid和group以及配置格式、配置内容信息，之后点击发布，即可发布配置，此处需要注意dataid需要填写正确，也就是按照上面的格式填写，比如我在bootstrap.yml中配置的服务名为service-provider，没有配置当前环境的profile文件，那么我的dataid就写成service-provider就可以了，如果我配置了当前环境的profile文件为dev，且配置了后缀为.yml，那么我dataid就要写成service-provider-dev.yml

   

   测试代码如下

   ~~~java
   package com.example.demo2.controller;
   
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.cloud.context.config.annotation.RefreshScope;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   /**
    * 测试配置能否自动更新
    */
   @RestController
   @RequestMapping("/nacos/config")
   //通过 Spring Cloud 原生注解 @RefreshScope 实现配置自动更新
   //这个注解需要加上，不然在注册中心中更新了配置，服务提供者和消费者不会自动更新配置
   @RefreshScope
   public class NacosConfigController {
       @Value("${useLocalCache}")
       private boolean useLocalCache;
   
       @RequestMapping("/get")
       public boolean get() {
           return useLocalCache;
       }
   }
   
   ~~~

   在浏览器中输入localhost:端口号/nacos/config/get即可获取刷新的配置信息

## 三、整合过程中踩得坑：

1. 由于nacos版本和springboot版本不匹配，导致各种异常报错，官方文档介绍如下

   nacos版本 [2.1.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-config) 对应的是 Spring Boot 2.1.x 版本。版本 [2.0.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-config) 对应的是 Spring Boot 2.0.x 版本，版本 [1.5.x.RELEASE](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-config) 对应的是 Spring Boot 1.5.x 版本

   经个人测试，我使用的版本信息如下，如下版本配置是可以正常运行项目，使用nacos的：

   nacos版本为2.1.2

   springboot版本为2.3.0.RELEASE

   依赖版本信息如下

   ~~~xml
          <!--配置中心 如果你只想使用nacos的配置中心功能，只引入此依赖包就可以-->
   		<dependency>
               <groupId>com.alibaba.cloud</groupId>
               <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
               <version>2.2.9.RELEASE</version>
           </dependency>
           <!--注册中心 如果你想使用nacos的注册中心，服务发现功能，还需要引入此依赖包-->
           <dependency>
               <groupId>com.alibaba.cloud</groupId>
               <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
               <version>2.2.9.RELEASE</version>
           </dependency>
   ~~~

   

以上仅是个人学习过程中的一些记录心得，如有不对的地方，欢迎大家讨论指正