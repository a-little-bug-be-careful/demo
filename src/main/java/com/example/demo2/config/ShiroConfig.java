package com.example.demo2.config;

import com.example.demo2.shiro.CustomerRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    //ShiroFilter过滤所有请求
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //给ShiroFilter配置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //配置系统受限资源
        //配置系统公共资源
        Map<String, String> map = new HashMap<String, String>();
        map.put("/log","authc");//表示这个资源需要认证和授权
        // 设置认证界面路径
        shiroFilterFactoryBean.setLoginUrl("/");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }
    //创建安全管理器
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getRealm());
        return securityManager;
    }
    //创建自定义Realm
    @Bean
    public Realm getRealm() {
        CustomerRealm realm = new CustomerRealm();
        return realm;
    }
}
