package com.example.demo2.controller;

import com.example.demo2.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
class RabbitConsumerController1 {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumerController1.class);
    @RabbitListener(queues = "TestDirectQueue1")
    public void getMsg(List<User> list) {
        list.stream().forEach(a -> {
            logger.info("receive java bean info from TestDirectQueue1: id {}; name {}; sex {}; age {}", a.getId(), a.getName(), a.getSex(), a.getAge());
        });
    }
}