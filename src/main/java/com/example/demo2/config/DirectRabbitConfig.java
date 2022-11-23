package com.example.demo2.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

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
        //设置队列中消息的过期时间
        Map<String, Object> argsMap = new HashMap<>(3);
        argsMap.put("x-message-ttl", 30000);//队列中的消息未被消费则30秒后过期
        /**死信队列设置：存放死信消息
         * 死信消息产生原因
         * 消息 TTL 过期
         * 队列达到最大长度(队列满了，无法再添加数据到 mq 中)
         * 消息被拒绝(basic.reject 或 basic.nack)并且 requeue=false
         */
        //声明当前队列绑定的死信交换机
        argsMap.put("x-dead-letter-exchange", "dead_exchange");
        //声明当前队列的死信路由 key
        argsMap.put("x-dead-letter-routing-key", "dead");
        return new Queue("TestDirectQueue1",true, false, false, argsMap);
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
        Map<String, Object> argsMap = new HashMap<>(1);
        //绑定备份交换机
        argsMap.put("alternate-exchange", "alternative_exchange");
        return new DirectExchange("TestDirectExchange1",true,false, argsMap);
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

 /*   @Bean
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

    @Bean
    DirectExchange deadExchange() {
        //声明死信交换机
        return new DirectExchange("dead_exchange", true, false);
    }

    @Bean
    Queue deadQueue() {
        //声明死信队列
        return new Queue("dead_queue", true, false, false);
    }

    @Bean
    Queue alternativeQueue() {
        //声明备份队列
        return new Queue("alternative_queue", true, false, false);
    }

    @Bean
    FanoutExchange alternativeExchange() {
        //声明备份交换机
        return new FanoutExchange("alternative_exchange", true, false);
    }

    @Bean
    Binding alternativeBinding() {
        //将备份交换机和备份队列绑定
        return BindingBuilder.bind(alternativeQueue()).to(alternativeExchange());
    }
}
