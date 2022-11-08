package com.example.demo2.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {

    /**  一个比较好的学习rabbitmq的网址
     * https://blog.csdn.net/AhangA/article/details/121641034
     **/

    //队列 起名：TestDirectQueue
    @Bean
    public Queue TestDirectQueue0() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参数优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("TestDirectQueue0",true);
    }

    @Bean
    public Queue TestDirectQueue1() {
        return new Queue("TestDirectQueue1",true);
    }

    /**
     * Direct交换机 起名：TestDirectExchange（默认类型的交换机，匹配发送）
     * 它会把消息路由到那些binding key与routing key完全匹配的Queue中。
     * 它是一个一对一的模型，一条消息一定会被发到指定的一个队列（完全匹配）
     * @return
     */
    @Bean
    DirectExchange TestDirectExchange0() {
        return new DirectExchange("TestDirectExchange0",true,false);
    }

    @Bean
    DirectExchange TestDirectExchange1() {
        return new DirectExchange("TestDirectExchange1",true,false);
    }

    /**绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
     * routingkey：消息生产者将消息发送到指定routingkey的交换机  rabbitTemplate.convertAndSend("TestDirectExchange0", "TestDirectRouting", JSONObject.toJSONString(users))
     * bindingkey：将交换机和队列绑定，其实bindingkey并不是真实存在的，真实情况下都是routingkey，交换机接收到消息后，根据消息中携带的routingkey，匹配对应的bindingkey，将消息发送到匹配成功的队列中
     * @return
     */
    @Bean
    Binding bindingDirect() {
        //bindingkey
        return BindingBuilder.bind(TestDirectQueue0()).to(TestDirectExchange0()).with("TestDirectRouting");
    }

/*    @Bean
    Binding bindingDirect1() {
        return BindingBuilder.bind(TestDirectQueue1()).to(TestDirectExchange0()).with("TestDirectRouting");
    }*/

    @Bean
    Binding bindingDirect2() {
        return BindingBuilder.bind(TestDirectQueue1()).to(TestDirectExchange1()).with("TestDirectRouting");
    }


    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }
}
