package com.example.demo2.controller;

import com.example.demo2.domain.User1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class RabbitConsumerController2 {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumerController2.class);

    @RabbitListener(queues = "topic_queueq2", containerFactory = "rabbitListenerContainerFactory1")
    public void getMsg(List<User1> list) {
        logger.info("receive json data: {}", list);
        list.stream().forEach(a -> {
            logger.info("receive json data info from topic_queueq2: id {}; name {}; sex {}; age {}", a.getId(), a.getName(), a.getSex(), a.getAge());
        });
    }
}
