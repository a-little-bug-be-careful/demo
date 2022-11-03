package com.example.demo2.controller;

import com.example.demo2.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitController {
    private static Logger logger = LoggerFactory.getLogger(RabbitController.class);
    @Autowired
    private RabbitMqService rabbitMqService;

    @PostMapping("/rabbitmq")
    public String rabbitMqTest() {
        rabbitMqService.sendMsg();
        try {
            //睡眠30秒，观察消息队列变化情况
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            logger.error("err: {}", interruptedException.getMessage());
        }
        return rabbitMqService.receiveMsg();
    }
}
