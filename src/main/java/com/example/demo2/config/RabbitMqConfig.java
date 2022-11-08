package com.example.demo2.config;

import org.springframework.context.annotation.Configuration;

/**
 * 配置rabbitmq消息队列的序列化和反序列化
 */
@Configuration
public class RabbitMqConfig {
    //默认的 SimpleMessageConverter 在发送消息时会将对象序列化成字节数组，若要反序列化对象，需要自定义 MessageConverter

}
