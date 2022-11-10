package com.example.demo2.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import com.example.demo2.domain.User1;
import com.example.demo2.mapper.UserMapper;
import com.example.demo2.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
        /**
         * 源码简单剖析：
         * 此处通过调用rabbitTemplate.convertAndSend方法，会默认采用SimpleMessageConverter进行序列化，生成不同contenttype类型的byte数组，创建消息体
         * SimpleMessageConverter中有个createMessage方法，会根据传递的数据类型创建不同的消息
         * 1. 如果数据类型为byte数组，messageProperties.setContentType("application/octet-stream")
         * 2. 如果数据类型为string字符串，messageProperties.setContentType("text/plain")
         * 3. 如果数据类型为实现了Serializable接口的java对象，messageProperties.setContentType("application/x-java-serialized-object")
         */
        //消息通过交换机TestDirectExchange0发送到队列TestDirectQueue0和队列TestDirectQueue1
        //发送字符串消息
        rabbitTemplate.convertAndSend("TestDirectExchange0", "TestDirectRouting", "test string");
        //byte[]数组
        rabbitTemplate.convertAndSend("TestDirectExchange0", "TestDirectRouting", new byte[]{1, 2});
        //实现了Serializable接口的java对象
        rabbitTemplate.convertAndSend("TestDirectExchange0", "TestDirectRouting", users);
        //消息通过交换机TestDirectExchange1发送到队列TestDirectQueue0
        rabbitTemplate.convertAndSend("TestDirectExchange1", "TestDirectRouting", users);
        logger.info("sending msg by direct exchange end>>>>");
        return InvokeResponse.succ("消息发送成功");
    }

    @Override
    public InvokeResponse receiveMsgByDirectExchange() {
        List<User> users = null, users0 = null, users1 = null;
        logger.info("receiving msg by direct exchange begin>>>>");
        Object object = rabbitTemplate.receiveAndConvert("TestDirectQueue0");
        if (null != object) {
            users0 = JSONObject.parseObject(object.toString(), List.class);
            if (null == users0) {
                logger.info("received msg by direct exchange: 无可消费消息");
            } else {
                logger.info("received msg by direct exchange: {}", users0.toString());
            }
        }
        object = rabbitTemplate.receiveAndConvert("TestDirectQueue1");
        if (null != object) {
            users1 = JSONObject.parseObject(object.toString(), List.class);
            if (null == users1) {
                logger.info("received msg by direct exchange: 无可消费消息");
            } else {
                logger.info("received msg by direct exchange: {}", users1.toString());
            }
        }
        Map<String, List<User>> map = new HashMap<>(2);
        map.put("TestDirectQueue0", users0);
        map.put("TestDirectQueue1", users1);
        return InvokeResponse.succ("succ", map);
    }

    @Override
    public InvokeResponse sendMsgByTopicExchange(Integer id) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        List<User1> users = new ArrayList<>();
        User1 user1 = new User1();
        user1.setName("仙人之下我无敌");
        user1.setAge(18);
        user1.setSex("1");
        users.add(user1);
        user1 = new User1();
        user1.setName("仙人之上一换一");
        user1.setAge(20);
        user1.setSex("0");
        users.add(user1);
        logger.info("sending msg by topic exchange begin>>>>");
        //消息通过交换机topic_exchange发送到队列topic_queueq1和topic_queueq2
        rabbitTemplate.convertAndSend("topic_exchange", "test.topic.", users);
        //消息通过交换机topic_exchange发送到队列topic_queueq2
        rabbitTemplate.convertAndSend("topic_exchange", "test.hahaha.ok", users);
        logger.info("sending msg by direct exchange end>>>>");
        return InvokeResponse.succ("消息发送成功");
    }

    @Override
    public InvokeResponse receiveMsgByTopicExchange() {
        List<User> users1 = null;
        List<User> users2 = null;
        logger.info("receiving msg by topic exchange begin>>>>");
        Object obj1 = rabbitTemplate.receiveAndConvert("topic_queueq1");
        if (null != obj1) {
            users1 = JSONObject.parseObject(obj1.toString(), List.class);
            if (null == users1) {
                logger.info("received msg by topic exchange: 无可消费消息");
            } else
                logger.info("received msg by topic exchange: topic_queueq1: {}", users1.toString());
        }
        Object obj2 = rabbitTemplate.receiveAndConvert("topic_queueq2");
        if (null == obj2) {
            logger.info("received msg by topic exchange: 无可消费消息");
        } else {
            users2 = JSONObject.parseObject(obj2.toString(), List.class);
            if (null == users2) {
                logger.info("received msg by topic exchange: 无可消费消息");
            } else
                logger.info("received msg by topic exchange: topic_queueq2: {}", users2.toString());
        }
        Map<String, List<User>> result = new HashMap<>(2);
        result.put("users1", users1);
        result.put("users2", users2);
        return InvokeResponse.succ("succ", result);
    }

    @Override
    public InvokeResponse sendMsgByFanoutExchange(Integer id) {
        List<User> users = this.userMapper.selectUser(id);
        if (users.isEmpty()) {
            return InvokeResponse.fail("发送消息失败，未查询到id{" + id + "}的用户信息");
        }
        logger.info("sending msg by fanout exchange begin>>>>");
        //消息通过交换机topic_exchange发送到队列fanout_queueq1和fanout_queueq2
        rabbitTemplate.convertAndSend("fanout_exchange", "", JSONObject.toJSONString(users));
        logger.info("sending msg by fanout exchange end>>>>");
        return InvokeResponse.succ("消息发送成功");
    }

    @Override
    public InvokeResponse receiveMsgByFanoutExchange() {
        List<User> users1 = null, users2 = null;
        logger.info("receiving msg by fanout exchange begin>>>>");
        Object object = rabbitTemplate.receiveAndConvert("fanout_queueq1");
        if (null != object) {
            users1 = JSONObject.parseObject(object.toString(), List.class);
            if (null == users1) {
                logger.info("received msg by fanout exchange: 无可消费消息");
            } else
                logger.info("received msg by fanout exchange-fanout_queueq1: {}", users1.toString());
        }
        object = rabbitTemplate.receiveAndConvert("fanout_queueq2");
        if (null != object) {
            users2 = JSONObject.parseObject(object.toString(), List.class);
            if (null == users2) {
                return InvokeResponse.fail("无可消费消息");
            } else
                logger.info("received msg by fanout exchange-fanout_queueq2: {}", users2.toString());
        }
        Map<String, List<User>> map = new HashMap<>(2);
        map.put("fanout_queueq1", users1);
        map.put("fanout_queueq2", users2);
        return InvokeResponse.succ("succ", map);
    }

    @Override
    public InvokeResponse sendTtlMsgByDirectExchange() {
        //设置单条消息过期时间TTL，单位毫秒，如果队列也设置了过期时间，取两者之间最小值
        //如果 不设置 TTL，表示消息永远不会过期，如果将 TTL 设置为 0，则表示除非此时可以直接投递该消息到消费者，否则该消息将会被丢弃
        rabbitTemplate.convertAndSend("TestDirectExchange1", "TestDirectRouting", "1234", m -> {
            m.getMessageProperties().setExpiration("100000");
            return m;
        });
        return InvokeResponse.succ("消息发送成功");
    }
}
