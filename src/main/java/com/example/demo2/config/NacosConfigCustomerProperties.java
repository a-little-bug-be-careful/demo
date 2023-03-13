package com.example.demo2.config;

import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * nacos配置中心中自定义的属性都从这个bean中获取，统一管理
 */
@RefreshScope
@Component
@Data
public class NacosConfigCustomerProperties {
}
