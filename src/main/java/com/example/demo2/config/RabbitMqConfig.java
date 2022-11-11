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
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory1(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //使用 JSON 配置rabbitmq消息队列的序列化和反序列化，不需要java对象再必须实现Serializable接口
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        ////基于代码配置消息确认机制，优先级大于基于配置文件，小于基于注解
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory2(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //使用 JSON 配置rabbitmq消息队列的序列化和反序列化，不需要java对象再必须实现Serializable接口
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        ////基于代码配置消息确认机制，优先级大于基于配置文件，小于基于注解
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        return factory;
    }

    /**
     * 可以自定义rabbitTemplate，同时定义消息序列化方式为Jackson，配置发布确认回调
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
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    //如果消息未发送成功
                    LOGGER.error("消息发送失败，请重试");
                }
            }
        });
        //true:交换机无法将消息进行路由时(找不到任何路由，包括死信队列的路由和备份交换机的路由)，会将该消息返回给生产者
        //false:如果发现消息无法进行路由，则直接丢弃;默认false
        //但是如果消息过期了，也不会触发这个机制
        rabbitTemplate.setMandatory(true);
        //设置回退消息交给谁处理
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                LOGGER.error("--------无法路由，回退处理--------{}", new String(message.getBody()));
            }
        });
        return rabbitTemplate;
    }
}
