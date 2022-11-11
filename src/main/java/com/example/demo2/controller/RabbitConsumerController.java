package com.example.demo2.controller;

import com.example.demo2.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 消息监听的方式 使用@RabbitListener注解、@RabbitHandler注解
 * 1. 该注解可以使用在类上，可以监听指定队列，但是要和@RabbitHandler结合使用(RabbitConsumerController类中的方式)
 * 2. 直接在方法上使用，监听指定队列，这种比较适用于队列中消息类型单一的情形，比如都是java bean(RabbitConsumerController1类中的方式)
 */
@RestController
//@RabbitListener(queues = "TestDirectQueue0")
public class RabbitConsumerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumerController.class);

    /**
     * rabbitmq消息监听方法不要有返回值，如果有返回值，系统会报错@exception {Cannot determine ReplyTo message property value: "
     * 								+ "Request message does not contain reply-to property, " +
     * 								"and no default Exchange was set.}
     * 方法参数要和队列中存储的数据类型保持一致，根据	contenttype确定
     * 1. application/octet-stream 对应byte[]数组
     * 2. text/plain 对应string字符串
     * 3. application/x-java-serialized-object 对应实现了Serializable接口的java对象
     * @param list
     */

    //获取实现了Serializable接口的java对象
    @RabbitHandler
    public void getMsg2(List<User> list) {
        list.stream().forEach(a -> {
            LOGGER.info("receive java bean info from TestDirectQueue0: id {}; name {}; sex {}; age {}", a.getId(), a.getName(), a.getSex(), a.getAge());
        });
    }

    //获取字符串消息
    @RabbitHandler
    public void getMsgStr(String str) {
        LOGGER.info("receive string msg from TestDirectQueue0: {}", str);
    }

    //获取byte[]数组消息
    @RabbitHandler
    public void getBytes(byte[] bytes) {
        LOGGER.info("receive byte[] msg from TestDirectQueue0: {}", bytes);
    }
}