package com.example.demo2.controller;

import com.example.demo2.domain.User1;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class RabbitConsumerController2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumerController2.class);

    /**
     * 这里的ackMode = "MANUAL"代表手动确认消息，MANUAL要大写，否则启动会报{找不到枚举信息}
     * 基于注解配置的消息确认机制优先级最高，> 基于代码配置 > 基于配置文件配置
     * @param list
     */
    @RabbitListener(queues = "topic_queueq2", containerFactory = "rabbitListenerContainerFactory1", ackMode = "MANUAL")
    public void getMsg(List<User1> list, Channel channel, Message message) {
        LOGGER.info("getMsg receive json data: {}", list);
        list.stream().forEach(a -> {
            LOGGER.info("getMsg receive json data info from topic_queueq2: id {}; name {}; sex {}; age {}", a.getId(), a.getName(), a.getSex(), a.getAge());
        });

        try {
            //手动消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ioException) {
            LOGGER.error("getMsg ack exception {}", ioException.getMessage());
        }

        LOGGER.info("getMsg msg deal done");
    }

    @RabbitListener(queues = "topic_queueq2", containerFactory = "rabbitListenerContainerFactory2", ackMode = "MANUAL")
    public void getMsg1(List<User1> list, Channel channel, Message message) {
        LOGGER.info("getMsg1 receive json data: {}", list);
        list.stream().forEach(a -> {
            LOGGER.info("getMsg1 receive json data info from topic_queueq2: id {}; name {}; sex {}; age {}", a.getId(), a.getName(), a.getSex(), a.getAge());
        });

        try {
            //手动消息确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ioException) {
            LOGGER.error("getMsg1 ack exception {}", ioException.getMessage());
        }

        LOGGER.info("getMsg1 msg deal done");
    }
}
