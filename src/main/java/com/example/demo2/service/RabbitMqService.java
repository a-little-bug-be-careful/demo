package com.example.demo2.service;

import com.example.demo2.domain.InvokeResponse;

public interface RabbitMqService {

    InvokeResponse sendMsg(Integer id);

    InvokeResponse receiveMsg();
}
