package com.example.demo2.service.impl;

import com.example.demo2.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqServiceImpl.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void sendMsg() {
        logger.info("begin sending msg>>>>");
        ConcurrentHashMap hashMap = new ConcurrentHashMap();
        hashMap.put(1, "小明");
        hashMap.put(2, "小白");
        hashMap.put(3, "小红");
        rabbitTemplate.convertAndSend("swl.direct", "1", hashMap);
    }

    @Override
    public String receiveMsg() {
        logger.info("begin receiving msg>>>>");
        Object o = rabbitTemplate.receiveAndConvert("swl.direct");
        logger.info("received msg: {}", o.toString());
        return o.toString();
    }
}
