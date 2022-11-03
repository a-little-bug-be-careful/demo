package com.example.demo2.service;

public interface RabbitMqService {

    void sendMsg();

    String receiveMsg();
}
