package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.SysUser;
import com.example.demo2.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public InvokeResponse login(@RequestBody SysUser sysUser) {
        if (StringUtils.isBlank(sysUser.getUserName())) {
            return InvokeResponse.fail("用户名不能为空");
        }
        if (StringUtils.isBlank(sysUser.getPassWord())) {
            return InvokeResponse.fail("密码不能为空");
        }
        int result = sysUserService.checkExistSysUser(sysUser);
        if (result > 0) {
            return InvokeResponse.succ("登录成功");
        } else {
            return InvokeResponse.fail("登录失败，用户名密码错误");
        }
    }
}
