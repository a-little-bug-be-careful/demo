package com.example.demo2.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * nacos配置中心中自定义的属性都从这个bean中获取，统一管理
 */
@RefreshScope
@Component
@Data
public class NacosConfigCustomerProperties {
    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Value("${spring.data.mongodb.authenticationDatabase}")
    private String mongoAuthentication;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${IMAGE_PATH}")
    private String imagePath;
}
