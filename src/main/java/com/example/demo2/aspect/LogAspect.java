package com.example.demo2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static Logger logger = LoggerFactory.getLogger(LogAspect.class);
    @Pointcut("execution(com.example.demo2.domain.InvokeResponse com.example.demo2.controller.*.*(..))")
    public void log() {

    }

    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) {
        logger.info("{} is invoked", joinPoint.getSignature());
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("{} exception msg", joinPoint.getSignature(), e);
            return null;
        }
    }
}
