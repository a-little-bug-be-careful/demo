

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

1. directexchange交换机

   rabbitmq的默认交换机类型，发送消息时需要指定routingkey，创建交换机时需要指定bindingkey，它会把消息路由到那些bindingkey和routingkey相同的队列中，其实bindingkey和routingkey只是便于区分，二者实际上是一个东西，在rabbitmq中其实只有routingkey。但此模式并不一定是一对一模式，一个交换机可以使用同一个bindingkey绑定多个队列。

   - direct交换机配置类

     ~~~java
     package com.example.demo2.config;
     
     import org.springframework.amqp.core.*;
     import org.springframework.context.annotation.Bean;
     import org.springframework.context.annotation.Configuration;
     import java.util.HashMap;
     import java.util.Map;
     
     @Configuration
     public class DirectRabbitConfig {
         /**
         最简单的一个队列
         **/
         @Bean
         public Queue TestDirectQueue() {
             // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效，rabbitmq服务重启后队列被删除
             // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参数优先级高于durable
             // autoDelete:默认false，是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
             return new Queue("TestDirectQueue1",true, false, false, null);
             //等同于 return new Queue("TestDirectQueue",true);
         }
     
         /**
          * 声明一个Direct交换机：TestDirectExchange（默认类型的交换机，匹配发送）
          * 它会把消息路由到那些binding key与routing key完全匹配的Queue中。
          * 一条消息一定会被发到指定的一个队列（完全匹配）
          * @return
          */
         @Bean
         DirectExchange TestDirectExchange() {
             return new DirectExchange("TestDirectExchange",true,false);
         }
     
         /**绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
          * routingkey：消息生产者将消息发送到指定routingkey的交换机  
          * bindingkey：将交换机和队列绑定，其实bindingkey并不是真实存在的，真实情况下都是routingkey，交换机接收到消息后，根据消息中携带的routingkey，匹配对应的bindingkey，将消息发送到匹配成功的队列中
          * @return
          */
         @Bean
         Binding bindingDirect() {
             //构建者模式绑定队列和交换机
             return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRouting");
         }
     }
     
     ~~~

   - 消息发送

     ~~~java
     public InvokeResponse sendMsgByDirectExchange(Integer id) {
             List<User> users = this.userMapper.selectUser(id);
             if (users.isEmpty()) {
                 return InvokeResponse.fail("发送消息失败，未查询到id{" + id + "}的用户信息");
             }
             /**
              * 源码简单剖析：
              * 此处通过调用rabbitTemplate.convertAndSend方法，会默认采用SimpleMessageConverter进行序列化，生成不同contenttype类型的byte数组，创建消息体
              * SimpleMessageConverter中有个createMessage方法，会根据传递的数据类型创建不同的消息
              * 1. 如果数据类型为byte数组，messageProperties.setContentType("application/octet-stream")
              * 2. 如果数据类型为string字符串，messageProperties.setContentType("text/plain")
              * 3. 如果数据类型为实现了Serializable接口的java对象，messageProperties.setContentType("application/x-java-serialized-object")
              */
             //消息携带TestDirectRouting（也就是routingkey）通过交换机TestDirectExchange发送到队列TestDirectQueue
             //发送字符串消息
             rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", "test string");
             //byte[]数组
             rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", new byte[]{1, 2});
             //实现了Serializable接口的java对象
             rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", users);
         
         //注意如果没有自定义rabbitmqtemplate的序列化配置，发送的消息只能为这三种类型，尤其是java对象，如果对象没有实现序列化Serializable接口，发送消息时会报错SimpleMessageConverter only supports String, byte[] and Serializable payloads，意思就是默认的消息转换器只支持string、byte[]数组、序列化的消息实体；自定义的序列化配置，后续会介绍
             return InvokeResponse.succ("消息发送成功");
         }
     ~~~

   - 消息接收

     ~~~java
     Object object = rabbitTemplate.receiveAndConvert("TestDirectQueue");
     ~~~

     

2. fanoutexchange交换机

   fanout交换机无需通过路由关系就可以发送消息，将消息发送到所有绑定到fanout交换机上的队列中

   - fanout交换机配置

     ~~~java
     package com.example.demo2.config;
     
     import org.springframework.amqp.core.Binding;
     import org.springframework.amqp.core.BindingBuilder;
     import org.springframework.amqp.core.FanoutExchange;
     import org.springframework.amqp.core.Queue;
     import org.springframework.context.annotation.Bean;
     import org.springframework.context.annotation.Configuration;
     
     @Configuration
     public class FanOutRabbitConfig {
         //声明两个队列
         @Bean
         public Queue fanoutQueue1() {
             return new Queue("fanout_queueq1", true, false, false);
         }
     
         @Bean
         public Queue fanoutQueue2() {
             return new Queue("fanout_queueq2", true, false, false);
         }
     
         /**
          * Fanout 类型（广播发送）
          * 它会把所有发送到该Exchange的消息路由到所有与它绑定的Queue中。
          *
          * 它是一种一对多的类型，无法指定Binding Key，发送的一条消息会被发到绑定的所有队列。
          * @return
          */
         @Bean
         public FanoutExchange fanoutExchange() {
             return new FanoutExchange("fanout_exchange", true, false, null);
         }
     
         //将队列信息绑定到交换机
         @Bean
         public Binding fanoutBinding1() {
             return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
         }
     
         @Bean
         public Binding fanoutBinding2() {
             return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
         }
     }
     
     ~~~

     

   - 消息发送

     ~~~java
         public InvokeResponse sendMsgByFanoutExchange(Integer id) {
             List<User> users = this.userMapper.selectUser(id);
             if (users.isEmpty()) {
                 return InvokeResponse.fail("发送消息失败，未查询到id{" + id + "}的用户信息");
             }
             logger.info("sending msg by fanout exchange begin>>>>");
             //消息通过交换机topic_exchange发送到队列fanout_queueq1和fanout_queueq2
             rabbitTemplate.convertAndSend("fanout_exchange", "", JSONObject.toJSONString(users));
             logger.info("sending msg by fanout exchange end>>>>");
             return InvokeResponse.succ("消息发送成功");
         }
     ~~~

   - 消息接收

     ~~~java
     Object object = rabbitTemplate.receiveAndConvert("fanout_queueq1");
     ~~~

     

3. topic交换机

   topic交换机和direct交换机比较类似，但是区别于direct交换机的routingkey和bindingkey强匹配，topic交换机提供了更为灵活的路由配置，提供通配符来进行路由消息转发，消息路由可以通过 **.** 分割符分割，每个分开的独立部分都叫一个单词，例如**test.hahaha.#**由两个单词**test**、**hahaha**和**#**通配符构成发送消息时，路由需要按照规则进行匹配成功后，就可以转发到相应队列，比如路由为**test.hahaha.**或者**test.hahaha.abc**，但是**a.hahaha.**将找不到路由

   

   一共有两种通配符：***** 和 **#**

   > - *****：匹配一个单词
   > - **#**：匹配零个或多个单词

   - topic交换机配置

     ~~~java
     package com.example.demo2.config;
     
     import org.springframework.amqp.core.Binding;
     import org.springframework.amqp.core.BindingBuilder;
     import org.springframework.amqp.core.Queue;
     import org.springframework.amqp.core.TopicExchange;
     import org.springframework.context.annotation.Bean;
     import org.springframework.context.annotation.Configuration;
     
     @Configuration
     public class TopicRabbitConfig {
         @Bean
         public Queue topicQueue1() {
             return new Queue("topic_queueq1", true, false, false);
         }
     
         @Bean
         public Queue topicQueue2() {
             return new Queue("topic_queueq2", true, false, false);
         }
     
         /**
          * Topic类型（拓展匹配发送）
          *它是Direct类型的一种扩展，提供灵活的匹配规则。
          *
          * routing key为一个句点号 " . " 分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如"One.Two"
          * binding key与routing key一样也是句点号 " . " 分隔的字符串
          * binding key中可以存在两种特殊字符 " * " 与 " # " ，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）
          * @return
          */
         @Bean
         public TopicExchange topicExchange() {
             return new TopicExchange("topic_exchange", true, false, null);
         }
     
         @Bean
         public Binding topicBinding1() {
             return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("*.topic.*");
         }
     
         @Bean
         public Binding topicBinding2() {
             return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("#");
         }
     }
     
     ~~~

     

   - 发送消息

     ~~~java
     public InvokeResponse sendMsgByTopicExchange(Integer id) {
         	//设置消息序列化的方式为jackson序列化，之后发送消息时，java对象不需要再实现Serializable接口
             rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
         //此处的user1对象没有实现Serializable接口
             List<User1> users = new ArrayList<>();
             User1 user1 = new User1();
             user1.setName("仙人之下我无敌");
             users.add(user1);
             user1 = new User1();
             user1.setName("仙人之上一换一");
             users.add(user1);
             logger.info("sending msg by topic exchange begin>>>>");
             //消息通过交换机topic_exchange发送到队列topic_queueq1和topic_queueq2，匹配路由"*.topic.*"
             rabbitTemplate.convertAndSend("topic_exchange", "test.topic.", users);
             //消息通过交换机topic_exchange发送到队列topic_queueq2，匹配路由"#"
             rabbitTemplate.convertAndSend("topic_exchange", "test.hahaha.ok", users);
             logger.info("sending msg by direct exchange end>>>>");
             return InvokeResponse.succ("消息发送成功");
         }
     ~~~

     

   - 消息接收

     ~~~java
     Object obj1 = rabbitTemplate.receiveAndConvert("topic_queueq1");
     ~~~

## 四、消费者消息监听机制

1. 使用方法主动接收指定队列的消息

   ~~~java
   Object object = rabbitTemplate.receiveAndConvert("TestDirectQueue0")
   ~~~

   

2. @RabbitListener监听指定队列的消息

   @RabbitListener监听指定队列，如果不和@RabbitHandler联用，可以将注解加在方法上，使用如下

   ~~~java
   //注解不要忘了加，不一定是@RestController，可以是其他的注解比如@component，能被spring容器管理就行
   @RestController
   public class RabbitConsumerController2 {
       private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumerController2.class);
   /**
        * 这里的ackMode = "MANUAL"代表手动确认消息，是一种消息确认机制，MANUAL要大写，否则启动会报{找不到枚举信息}，基于注解配置的消息确认机制优先级最高，> 基于代码配置 > 基于配置文件配置
        * containerFactory指定消息消费者监听容器工厂，是一个配置类，可以自定义消息序列化机制等（在第五节会有详细介绍）
        * @param list
        */
       @RabbitListener(queues = "topic_queueq2", containerFactory = "rabbitListenerContainerFactory1", ackMode = "MANUAL")
       public void getMsg(List<User1> list, Channel channel, Message message) {
           LOGGER.info("getMsg receive json data: {}", list);
           try {
               //手动消息确认
               channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
           } catch (IOException ioException) {
               LOGGER.error("getMsg ack exception {}", ioException.getMessage());
           }
   
           LOGGER.info("getMsg msg deal done");
       }
   }
   ~~~

   

3. @RabbitListener和@RabbitHandler联合使用监听指定队列的消息

   @RabbitListener加在类上，监听指定的队列，可以监听多个队列，通过queues属性指定监听队列，@RabbitHandler加在方法上监听具体的队列，消费队列中的信息。@RabbitHandler注解的方法，通过方法参数匹配消费队列中的消息，如果方法参数为string，则消费对应的string信息；如果是byte[]数组，则消费对应的byte[]信息；如果是java对象，则消费对应的java对象信息

   ~~~java
   package com.example.demo2.controller;
   
   import com.example.demo2.domain.User;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.amqp.rabbit.annotation.RabbitHandler;
   import org.springframework.amqp.rabbit.annotation.RabbitListener;
   import org.springframework.web.bind.annotation.RestController;
   import java.util.List;
   
   /**
    * 消息监听的方式 使用@RabbitListener注解、@RabbitHandler注解
    * 1. 该注解可以使用在类上，可以监听指定队列，但是要和@RabbitHandler结合使用(RabbitConsumerController类中的方式)
    * 2. 直接在方法上使用，监听指定队列，这种比较适用于队列中消息类型单一的情形，比如都是java bean(RabbitConsumerController1类中的方式)
    */
   @RestController
   @RabbitListener(queues = "TestDirectQueue0")
   public class RabbitConsumerController {
       private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumerController.class);
   
       /**
        * rabbitmq消息监听方法不要有返回值，如果有返回值，系统会报错@exception {Cannot determine ReplyTo message property value: "
        * 								+ "Request message does not contain reply-to property, " +
        * 								"and no default Exchange was set.}
        * 方法参数要和队列中存储的数据类型保持一致，根据	contenttype确定
        * 1. application/octet-stream 对应byte[]数组
        * 2. text/plain 对应string字符串
        * 3. application/x-java-serialized-object 对应实现了Serializable接口的java对象
        * @param list
        */
   
       //获取实现了Serializable接口的java对象
       @RabbitHandler
       public void getMsg2(List<User> list) {
           list.stream().forEach(a -> {
               LOGGER.info("receive java bean info from TestDirectQueue0: id {}; name {}; sex {}; age {}", a.getId(), a.getName(), a.getSex(), a.getAge());
           });
       }
   
       //获取字符串消息
       @RabbitHandler
       public void getMsgStr(String str) {
           LOGGER.info("receive string msg from TestDirectQueue0: {}", str);
       }
   
       //获取byte[]数组消息
       @RabbitHandler
       public void getBytes(byte[] bytes) {
           LOGGER.info("receive byte[] msg from TestDirectQueue0: {}", bytes);
       }
   }
   ~~~

   

## 五、rabbitmq消息序列化

rabbitmq会默认采用SimpleMessageConverter进行序列化，将消息序列化为byte[]数组

* SimpleMessageConverter中有个createMessage方法，会根据传递的数据类型创建不同的消息
         1. 如果数据类型为byte数组，messageProperties.setContentType("application/octet-stream")
            2. 如果数据类型为string字符串，messageProperties.setContentType("text/plain")
            3. 如果数据类型为实现了Serializable接口的java对象，messageProperties.setContentType("application/x-java-serialized-object")
* 由于我们发送消息时，可能会经常使用java对象作为消息体，而使用默认的序列化机制，java对象需要实现Serializable接口，第一每个对象都实现该接口就比较繁琐，第二jdk自带的Serializable序列化机制可能会存在问题，比如它每次序列化时都会将整个类信息都序列化，包括类名等额外的我们其实并不需要的信息，导致序列化后的字节数组比较大，另外该种序列化机制在反序列化时可能会存在问题，比如发送端和接收端java对象不一致（发送端加了一个字段，接收端没加这个字段，而且没有指定serializationid），可能会导致反序列化失败；由于以上种种原因，我们可以自定义消息序列化机制，本文介绍Jackson2JsonMessageConverter序列化和反序列化。

1. 发送端消息序列化

   发送端消息序列化可以有两种方式

   - 在发送消息时，通过java代码指定序列化方式

     ~~~java
     //设置消息序列化	    
     rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
             List<User1> users = new ArrayList<>();
             User1 user1 = new User1();
             user1.setName("仙人之下我无敌");
             user1.setAge(18);
             user1.setSex("1");
             users.add(user1);
             user1 = new User1();
             user1.setName("仙人之上一换一");
             user1.setAge(20);
             user1.setSex("0");
             users.add(user1);
             logger.info("sending msg by topic exchange begin>>>>");
             //消息通过交换机topic_exchange发送到队列topic_queueq1和topic_queueq2
             rabbitTemplate.convertAndSend("topic_exchange", "test.topic.", users);
     ~~~

     

   - 通过配置类指定序列化方式

     ~~~java
     package com.example.demo2.config;
     
     import org.slf4j.Logger;
     import org.slf4j.LoggerFactory;
     import org.springframework.amqp.core.AcknowledgeMode;
     import org.springframework.amqp.core.Message;
     import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
     import org.springframework.amqp.rabbit.connection.ConnectionFactory;
     import org.springframework.amqp.rabbit.connection.CorrelationData;
     import org.springframework.amqp.rabbit.core.RabbitTemplate;
     import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
     import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
     import org.springframework.context.annotation.Bean;
     import org.springframework.context.annotation.Configuration;
     
     
     @Configuration
     public class RabbitMqConfig {
         /**
          * 可以自定义rabbitTemplate，同时定义消息序列化方式为Jackson
          * 自定义的配置会优先于rabbitmq的自动配置加载，由于自动配置类上有@ConditionalOnClass({RabbitTemplate.class, Channel.class})注解，
          * 所以自动配置类中的配置信息（比如RabbitTemplate）无效
          * 同一个配置类中不允许有两个id相同的bean
          * @return  RabbitTemplate类的一个实例，id为getRabbitTemplate
          *
          */
         @Bean
         public RabbitTemplate getRabbitTemplate(ConnectionFactory connectionFactory) {
             RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
             rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
             return rabbitTemplate;
         }
     }
     ~~~

     

2. 接收端消息序列化

   接收端消息序列化机制，一般通过配置类实现，代码如下

~~~java
package com.example.demo2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConfig.class);
    /**
     * 方法名最好使用rabbitListenerContainerFactory（默认的）
     * 否则需要在@RabbitListener(queues = "topic_queueq2", containerFactory = "rabbitListenerContainerFactory1")注解中指定
     */
    //推荐
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //使用 JSON 配置rabbitmq消息队列的序列化和反序列化，不需要java对象再必须实现Serializable接口
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
    
    //不是很推荐起名rabbitListenerContainerFactory1，除非需要配置多个监听机制
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory1(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //使用 JSON 配置rabbitmq消息队列的序列化和反序列化，不需要java对象再必须实现Serializable接口
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
~~~



## 六、rabbitmq消息丢失问题

## 七、rabbitmq消息重复消费问题





