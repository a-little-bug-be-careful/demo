package com.example.demo2.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用 JSON 配置rabbitmq消息队列的序列化和反序列化
 */
@Configuration
public class RabbitMqConfig {
    /**
     * 方法名最好使用rabbitListenerContainerFactory（默认的）
     * 否则需要在@RabbitListener(queues = "topic_queueq2", containerFactory = "rabbitListenerContainerFactory1")注解中指定
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory1(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
