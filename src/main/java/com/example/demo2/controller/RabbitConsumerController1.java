package com.example.demo2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
class RabbitConsumerController1 {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumerController1.class);
/*    @RabbitListener(queues = "TestDirectQueue1")
    public void getMsg(String msg) {
        try {
            logger.info("RabbitConsumerController1 sleeping 60s to test msg expiration time{30s}");
            Thread.sleep(60000);
        } catch (InterruptedException interruptedException) {

        }
        logger.info("receive msg info from TestDirectQueue1: {}", msg);
    }*/

    @RabbitListener( queues = "dead_queue")
    public void getDeadMsg(String msg) {
        logger.info("receive dead msg info from TestDirectQueue1: {}", msg);
    }
}