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

    @PostMapping("/sendmsg")
    public InvokeResponse sendMsg(Integer id) {
        return rabbitMqService.sendMsg(id);
    }

    @PostMapping("/getmsg")
    public InvokeResponse<List<User>> getMsg() {
        InvokeResponse<List<User>> response = rabbitMqService.receiveMsg();
        return response;
    }
}
