package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/key/value")
    public InvokeResponse set(String key, String value) {
        redisUtil.set(key, value, 100);
        return InvokeResponse.succ();
    }

    @GetMapping("/key")
    public InvokeResponse get(String key) {
        return InvokeResponse.succ(redisUtil.get(key));
    }

    @DeleteMapping("/key")
    public InvokeResponse delete(String key) {
        redisUtil.del(key);
        return InvokeResponse.succ();
    }

    @PutMapping("/key/value")
    public InvokeResponse update(String key, String value) {
        redisUtil.del(key);
        redisUtil.set(key, value, 100);
        return InvokeResponse.succ();
    }
}
