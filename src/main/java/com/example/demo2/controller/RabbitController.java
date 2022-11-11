package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import com.example.demo2.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/rabbit")
public class RabbitController {
    private static Logger logger = LoggerFactory.getLogger(RabbitController.class);
    @Autowired
    private RabbitMqService rabbitMqService;

    @PostMapping("/direct/msg")
    public InvokeResponse sendDirectMsg(Integer id) {
        return rabbitMqService.sendMsgByDirectExchange(id);
    }

    @GetMapping("/direct/msg")
    public InvokeResponse<List<User>> getDirectMsg() {
        InvokeResponse<List<User>> response = rabbitMqService.receiveMsgByDirectExchange();
        return response;
    }

    @PostMapping("/topic/msg")
    public InvokeResponse sendTopicMsg(Integer id) {
        return rabbitMqService.sendMsgByTopicExchange(id);
    }

    @GetMapping("/topic/msg")
    public InvokeResponse getTopicMsg() {
        return rabbitMqService.receiveMsgByTopicExchange();
    }

    @PostMapping("/fanout/msg")
    public InvokeResponse sendFanoutMsg(Integer id) {
        return rabbitMqService.sendMsgByFanoutExchange(id);
    }

    @GetMapping("/fanout/msg")
    public InvokeResponse getFanoutMsg() {
        return rabbitMqService.receiveMsgByFanoutExchange();
    }

    @PostMapping("/direct/msg/ttl")
    public InvokeResponse sendDirectTtlMsg() {
        return rabbitMqService.sendTtlMsgByDirectExchange();
    }

    @PostMapping("/fanout/msg/repeat")
    public InvokeResponse sendFanoutRepeatMsg() {
        return rabbitMqService.sendFanoutRepeatMsg();
    }
}
