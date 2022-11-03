package com.example.demo2.service;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    InvokeResponse selectUser(Integer id);
    InvokeResponse insertUser(User user);
    InvokeResponse deleteUser(Integer id);
    InvokeResponse editUser(User user);
}
