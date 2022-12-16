package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public InvokeResponse login() {
        return InvokeResponse.succ();
    }
}
