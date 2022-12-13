# docker安装rabbitmq并整合springboot

## 一、docker安装rabbitmq

**以下安装步骤基于centos7系统，且提前安装了docker**

1. 拉取rabbitmq镜像

   ~~~
   docker pull rabbitmq:3.9.8-management
   ~~~

2. 启动rabbitmq

   ~~~
   docker run -d -p 5672:5672 -p 15672:15672 --name myrabbitmq docker.io/rabbitmq:3.9.8-management
   # -p 5672:5672 标识linux与rabbitmq容器服务映射端口
   # -p 15672:15672 标识linux与rabbitmq容器客户端映射端口，外部通过此端口访问服务
   ~~~

3. 打开浏览器，输入ip地址:15672访问

   ![image-20221123135054667](C:\Users\Y05926\AppData\Roaming\Typora\typora-user-images\image-20221123135054667.png)

   初始密码用户名/密码：guest/guest



## 二、springboot整合rabbitmq

1. pom.xml文件导入相关依赖

   ~~~xml
   <!-- 整合rabbitmq -->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-amqp</artifactId>
               <version>2.5.5</version>
           </dependency>
   ~~~

2. 修改配置文件application.yml

   ~~~yaml
   spring:
     rabbitmq:
       host: 192.168.133.130
       port: 5672
       username: guest # rabbitmq用户名
       password: guest # rabbitmq用户密码
   ~~~





## 三、总结

本文仅是对docker安装rabbitmq和springboot整合rabbitmq的简单介绍，关于rabbitmq的在springboot项目中的使用，代码中有介绍，还没来得及整理，后期有空整理一版，仅此记录

## 四、参考文章

https://blog.csdn.net/qq_38837032/article/details/121000562