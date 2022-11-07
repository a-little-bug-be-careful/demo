package com.example.demo2.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import com.example.demo2.mapper.UserMapper;
import com.example.demo2.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqServiceImpl.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public InvokeResponse sendMsgByDirectExchange(Integer id) {
        List<User> users = this.userMapper.selectUser(id);
        if (users.isEmpty()) {
            return InvokeResponse.fail("发送消息失败，未查询到id{" + id + "}的用户信息");
        }
        logger.info("sending msg by direct exchange begin>>>>");
        //消息通过交换机TestDirectExchange0发送到队列TestDirectQueue0和队列TestDirectQueue1
        rabbitTemplate.convertAndSend("TestDirectExchange0", "TestDirectRouting", JSONObject.toJSONString(users));
        //消息通过交换机TestDirectExchange1发送到队列TestDirectQueue0
        rabbitTemplate.convertAndSend("TestDirectExchange1", "TestDirectRouting", JSONObject.toJSONString(users));
        logger.info("sending msg by direct exchange end>>>>");
        return InvokeResponse.succ("消息发送成功");
    }

    @Override
    public InvokeResponse receiveMsgByDirectExchange() {
        logger.info("receiving msg by direct exchange begin>>>>");
        Object object = rabbitTemplate.receiveAndConvert("TestDirectQueue0");
        List<User> users = JSONObject.parseObject(object.toString(), List.class);
        if (null == users) {
            return InvokeResponse.fail("无可消费消息");
        }
        logger.info("received msg by direct exchange: {}", users.toString());
        return InvokeResponse.succ("succ", users);
    }

    @Override
    public InvokeResponse sendMsgByTopicExchange(Integer id) {
        List<User> users = this.userMapper.selectUser(id);
        if (users.isEmpty()) {
            return InvokeResponse.fail("发送消息失败，未查询到id{" + id + "}的用户信息");
        }
        logger.info("sending msg by topic exchange begin>>>>");
        //消息通过交换机topic_exchange发送到队列topic_queueq1和topic_queueq2
        rabbitTemplate.convertAndSend("topic_exchange", "test.topic.", JSONObject.toJSONString(users));
        //消息通过交换机topic_exchange发送到队列topic_queueq2
        rabbitTemplate.convertAndSend("topic_exchange", "test.hahaha.ok", JSONObject.toJSONString(users));
        logger.info("sending msg by direct exchange end>>>>");
        return InvokeResponse.succ("消息发送成功");
    }

    @Override
    public InvokeResponse receiveMsgByTopicExchange() {
        logger.info("receiving msg by topic exchange begin>>>>");
        Object obj1 = rabbitTemplate.receiveAndConvert("topic_queueq1");
        List<User> users1 = JSONObject.parseObject(obj1.toString(), List.class);
        if (null == users1) {
            return InvokeResponse.fail("无可消费消息");
        }
        Object obj2 = rabbitTemplate.receiveAndConvert("topic_queueq2");
        List<User> users2 = JSONObject.parseObject(obj2.toString(), List.class);
        if (null == users2) {
            return InvokeResponse.fail("无可消费消息");
        }
        logger.info("received msg by topic exchange: topic_queueq1: {}, topic_queueq1: {}", users1.toString(), users2.toString());
        Map<String, List<User>> result = new HashMap<>();
        result.put("users1", users1);
        result.put("users2", users2);
        return InvokeResponse.succ("succ", result);
    }

    @Override
    public InvokeResponse sendMsgByFanoutExchange(Integer id) {
        return null;
    }

    @Override
    public InvokeResponse receiveMsgByFanoutExchange() {
        return null;
    }
}
