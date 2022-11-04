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
import java.util.List;

@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqServiceImpl.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public InvokeResponse sendMsg(Integer id) {
        List<User> users = this.userMapper.selectUser(id);
        if (users.isEmpty()) {
            return InvokeResponse.fail("发送消息失败，未查询到id{" + id + "}的用户信息");
        }
        logger.info("sending msg begin>>>>");
        //消息通过交换机0发送到队列0和队列1
        rabbitTemplate.convertAndSend("TestDirectExchange0", "TestDirectRouting", JSONObject.toJSONString(users));
        //消息通过交换机1发送到队列0
        rabbitTemplate.convertAndSend("TestDirectExchange1", "TestDirectRouting", JSONObject.toJSONString(users));
        logger.info("sending msg end>>>>");
        return InvokeResponse.succ("消息发送成功");
    }

    @Override
    public InvokeResponse receiveMsg() {
        logger.info("receiving msg begin>>>>");
        Object object = rabbitTemplate.receiveAndConvert("TestDirectQueue0");
        List<User> users = JSONObject.parseObject(object.toString(), List.class);
        if (null == users) {
            return InvokeResponse.fail("无可消费消息");
        }
        logger.info("received msg: {}", users.toString());
        return InvokeResponse.succ("succ", users);
    }
}
