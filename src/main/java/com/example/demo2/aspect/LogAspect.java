package com.example.demo2.aspect;

import com.example.demo2.domain.InvokeResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
/**
 * 配置一个切面捕获controller中返回值为InvokeResponse的方法异常信息
 */
public class LogAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    /**
     * com.example.demo2.domain.InvokeResponse 方法返回值类型
     * com.example.demo2.controller.*.*(..) 捕获com.example.demo2.controller文件夹下中所有controller的所有方法
     */
    @Pointcut("execution(com.example.demo2.domain.InvokeResponse com.example.demo2.controller.*.*(..))")
    public void log() {

    }

    /**
     * 环绕
     * @param joinPoint
     * @return
     */
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) {
        LOGGER.info("{} is invoked", joinPoint.getSignature());
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            LOGGER.error("{} exception msg", joinPoint.getSignature(), e);
            return InvokeResponse.fail(e.getMessage());
        }
    }
}
