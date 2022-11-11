package com.example.demo2.controller;

import com.example.demo2.util.RedisUtil;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

/**
 * 监听fanout_queueq3和fanout_queueq4中的消息
 * 解决消息重复消费的问题
 */
@RestController
public class RabbitConsumerController3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumerController3.class);

    @Autowired
    private RedisUtil redisUtil;

    //获取字符串消息
    @RabbitListener(queues = "fanout_queueq3", containerFactory = "rabbitListenerContainerFactory1", ackMode = "MANUAL")  //不要直接绑定队列，否则只有一个消费者能消费到消息
    public void getMsgStr1(Channel channel, Message message) {
        String str = new String(message.getBody());
        LOGGER.info("getMsgStr1 receive string msg from fanout_queueq3: {}", str);
        try {
            //手动消息确认，告诉消息队列已成功消费消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ioException) {
            LOGGER.error("getMsg ack exception {}", ioException.getMessage());
        }
    }

    //获取字符串消息
    @RabbitListener(queues = "fanout_queueq4", containerFactory = "rabbitListenerContainerFactory1", ackMode = "MANUAL")  //不要直接绑定队列，否则只有一个消费者能消费到消息
    public void getMsgStr2(Channel channel, Message message) {
        try {
            String str = new String(message.getBody());
            String msgId = message.getMessageProperties().getMessageId();
            if (null != redisUtil.get("fanout_queueq4")) {
                //如果redis中没有该消息，认为没有被消费过
                if (StringUtils.equals(redisUtil.get("fanout_queueq4").toString(), msgId)) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }
            }
            LOGGER.info("getMsgStr2 receive string msg from fanout_queueq4: {}", str);
            //将消息id放入redis，并设置过期时间
            redisUtil.set("fanout_queueq4", msgId, 30);
            //手动消息确认，告诉消息队列消费消息失败，模拟{多个消费者消费同一个队列中同一条消息时，其中一个消费者消费消息失败}的情形
            //最后一个参数{true}表示让消息重回队列;{false}表示丢弃该消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        } catch (IOException ioException) {
            LOGGER.error("getMsg ack exception {}", ioException.getMessage());
        }
    }
}