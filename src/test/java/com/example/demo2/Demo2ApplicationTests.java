package com.example.demo2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class Demo2ApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void redisTest() {
        redisTemplate.opsForValue().set("str1", "hhhh", 5, TimeUnit.SECONDS);
        redisTemplate.opsForValue().setIfAbsent("str2", "lalala", 10, TimeUnit.SECONDS);
        List<String> list = redisTemplate.opsForValue().multiGet(Arrays.asList("str1","str2"));
        list.stream().forEach(a -> System.out.println(a));
        redisTemplate.opsForList().leftPushAll("list1", Arrays.asList("1", "2", "3", "4"));
        list = redisTemplate.opsForList().range("list1", 0, 4);
        list.stream().forEach(a -> System.out.println(a));
    }

}
