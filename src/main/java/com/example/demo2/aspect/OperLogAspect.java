package com.example.demo2.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.model.RestResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OperLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperLogAspect.class);

    @Pointcut("@annotation(com.example.demo2.annotation.OperLog)")
    public void operLogPointCut() {

    }

    @Around("operLogPointCut()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) {
        LOGGER.info("test annotation operlog");
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("{} execution result: {}", joinPoint.getSignature(), JSON.toJSONString(result));
            return result;
        } catch (Throwable e) {
            LOGGER.error("{} exception msg: {}", joinPoint.getSignature(), JSON.toJSONString(e));
        }
        return null;
    }

}
