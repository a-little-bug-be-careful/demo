package com.example.demo2.service;

import com.example.demo2.domain.InvokeResponse;

public interface RabbitMqService {

    InvokeResponse sendMsgByDirectExchange(Integer id);

    InvokeResponse receiveMsgByDirectExchange();

    InvokeResponse sendMsgByTopicExchange(Integer id);

    InvokeResponse receiveMsgByTopicExchange();

    InvokeResponse sendMsgByFanoutExchange(Integer id);

    InvokeResponse receiveMsgByFanoutExchange();

    InvokeResponse sendTtlMsgByDirectExchange();

    InvokeResponse sendFanoutRepeatMsg();
}
