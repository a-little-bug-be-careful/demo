

# rabbitmq学习总结

## 一、准备工作

docker安装部署rabbitmq以及springboot整合rabbitmq请参考 **/readme/RABBITMQ.md**

## 二、rabbitmq简介

1. MQ存在的意义

   - 异步：生产者通过将请求发送到消息队列，不需要等结果返回，由消息队列将请求分发到消费者，消息消费成功后，结果返回给生产者
   - 解耦：服务a调用服务b的接口，不再直接调用，服务a将调用接口请求发送到消息队列，由消息队列转发请求到服务b来进行调用，这样两个系统之间只要约定好接口规范，就可以通过消息队列实现解耦
   - 削峰：一段时间内，请求量剧增，服务b的压力会突然增大，通过消息队列的容量限制请求数，达到削峰的目的，多余的请求可以放在其他队列进行等待

2. 消息服务的两个规范

   - JMS：java message server ---基于jvm消息代理的规范，activemq、hornetmq是jms的实现，基于java，非跨语言、跨平台
   - AMQP：advanced message queuing protocol ---高级消息队列协议，也是一个消息代理的规范，兼容JMS，rabbitmq就是其实现，跨语言、跨平台

3. rabbitmq基本构成

   - message：消息，基于byte[]数组传递消息，可以自定义消息序列化

   - publisher：消息生产者

   - exchange：消息交换机，rabbitmq包含四种类型

     > - direct交换机：点对点交换机，rabbitmq默认的交换机类型，根据路由键传递消息
     > - fanout交换机：扇出、广播交换机，不依赖于路由
     > - topic交换机：话题，类似于direct交换机，但是匹配机制更为灵活，提供通配符路由
     > - headers交换机：不通过路由，使用amqp的消息头判断，使用比较少

   - queue：队列

   - binding：绑定，将交换机和队列绑定

   - connection：网络连接，如tcp

   - channel：信道，多路复用机制，每次都建立tcp连接比较消耗资源，一个tcp连接可以创建多个信道

   - consumer：消费者

   - virtual host：虚拟主机，每一个虚拟主机都是一个mini版本的rabbitmq，拥有独立的队列、交换机、绑定等。默认的virtual host是 /。这个机制可以让一个rabbitmq服务器，虚拟出多个虚拟的消息队列服务器，不同需求的应用使用不同的virtual host

## 三、rabbitmq交换机类型

## 四、rabbitmq消息丢失问题

## 五、rabbitmq消息重复消费问题





