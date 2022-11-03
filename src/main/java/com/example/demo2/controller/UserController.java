package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import com.example.demo2.service.RabbitMqService;
import com.example.demo2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    public InvokeResponse getUser(Integer id) {
        return this.userService.selectUser(id);
    }

    @PostMapping("/addUser")
    public InvokeResponse addUser(User user) {
        return this.userService.insertUser(user);
    }

    @PostMapping("/deleteUser")
    public InvokeResponse deleteUser(Integer id) {
        return this.userService.deleteUser(id);
    }

    @PostMapping("/editUser")
    public ResponseEntity<InvokeResponse> editUser(User user) {
        InvokeResponse response = null;
        if (null == user || null == user.getId()) {
            response = new InvokeResponse("0", "更新用户信息失败，用户id为空");
        } else {
            response = this.userService.editUser(user);
        }
        return new ResponseEntity<InvokeResponse>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/test")
    public String test() {
        throw new RuntimeException();
    }

    @PostMapping("/test1")
    @ResponseStatus(HttpStatus.FOUND)
    public InvokeResponse test1() {
        return InvokeResponse.succ("succ");
    }
}
