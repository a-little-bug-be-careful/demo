package com.example.demo2.service;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    InvokeResponse selectUser(String id);
    InvokeResponse insertUser(User user);
    InvokeResponse deleteUser(String id);
    InvokeResponse editUser(User user);
}
