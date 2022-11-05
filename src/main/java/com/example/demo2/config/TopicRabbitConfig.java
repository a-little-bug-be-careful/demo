package com.example.demo2.config;

import jdk.internal.icu.impl.UBiDiProps;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {
    @Bean
    public Queue queue1() {
        return new Queue("topic_queueq1", true, false, false);
    }

    @Bean
    public Queue queue2() {
        return new Queue("topic_queueq2", true, false, false);
    }

    /**
     * Topic类型（拓展匹配发送）
     *它是Direct类型的一种扩展，提供灵活的匹配规则。
     *
     * routing key为一个句点号 " . " 分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如"One.Two"
     * binding key与routing key一样也是句点号 " . " 分隔的字符串
     * binding key中可以存在两种特殊字符 " * " 与 " # " ，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）
     * ————————————————
     * 版权声明：本文为CSDN博主「收藏=学会了」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/AhangA/article/details/121641034
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic_exchange", true, false, null);
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(queue1()).to(topicExchange()).with("*.two.*");
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(queue2()).to(topicExchange()).with("#");
    }
}
