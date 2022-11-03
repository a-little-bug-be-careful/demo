package com.example.demo2.aspect;

import com.example.demo2.controller.HelloController;
import com.example.demo2.domain.InvokeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {HelloController.class})
public class MainExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public InvokeResponse userNotFound() {
        InvokeResponse invokeResponse = new InvokeResponse();
        invokeResponse.setMsg("未查询到用户信息");
        invokeResponse.setCode("2003");
        return invokeResponse;
    }
}
