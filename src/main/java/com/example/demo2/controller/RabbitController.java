package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import com.example.demo2.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class RabbitController {
    private static Logger logger = LoggerFactory.getLogger(RabbitController.class);
    @Autowired
    private RabbitMqService rabbitMqService;

    @PostMapping("/direct/sendmsg")
    public InvokeResponse sendDirectMsg(Integer id) {
        return rabbitMqService.sendMsgByDirectExchange(id);
    }

    @PostMapping("/direct/getmsg")
    public InvokeResponse<List<User>> getDirectMsg() {
        InvokeResponse<List<User>> response = rabbitMqService.receiveMsgByDirectExchange();
        return response;
    }

    @PostMapping("/topic/sendmsg")
    public InvokeResponse sendTopicMsg(Integer id) {
        return rabbitMqService.sendMsgByTopicExchange(id);
    }

    @PostMapping("/topic/getmsg")
    public InvokeResponse getTopicMsg() {
        return rabbitMqService.receiveMsgByTopicExchange();
    }

    @PostMapping("/fanout/sendmsg")
    public InvokeResponse sendFanoutMsg(Integer id) {
        return rabbitMqService.sendMsgByFanoutExchange(id);
    }

    @PostMapping("/fanout/getmsg")
    public InvokeResponse getFanoutMsg() {
        return rabbitMqService.receiveMsgByFanoutExchange();
    }
}
