package com.example.demo2.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanOutRabbitConfig {
    @Bean
    public Queue queue1() {
        return new Queue("fanout_queueq1", true, false, false);
    }

    @Bean
    public Queue queue2() {
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

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(queue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(queue2()).to(fanoutExchange());
    }
}
