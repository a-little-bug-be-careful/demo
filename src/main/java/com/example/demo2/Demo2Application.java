package com.example.demo2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.example.demo2.mapper")
@EnableDiscoveryClient//通过 Spring Cloud 原生注解 @EnableDiscoveryClient 开启服务注册发现功能，经测试不加这个注解也可以被nacos注册中心发现
public class Demo2Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }

}
