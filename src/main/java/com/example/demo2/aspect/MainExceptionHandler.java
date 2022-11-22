package com.example.demo2.aspect;

import com.example.demo2.controller.UserController;
import com.example.demo2.domain.InvokeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 专门捕获controller中指定类型的异常信息
 */
@RestControllerAdvice(assignableTypes = {UserController.class})
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
