package com.example.demo2.controller;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.SysUser;
import com.example.demo2.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/manage")
public class UserManageController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/users")
    public InvokeResponse<List<SysUser>> getSysUserList(SysUser sysUser) {
        List<SysUser> users = sysUserService.getSysUserList(sysUser);
        return InvokeResponse.succ(users);
    }

    @GetMapping("/user")
    public InvokeResponse<SysUser> getSysUserById(Integer id) {
        SysUser sysUser = sysUserService.getSysUserById(id);
        return InvokeResponse.succ(sysUser);
    }

    @PostMapping("/user")
    public InvokeResponse insertSysUser(@RequestBody SysUser sysUser) {
        sysUserService.insertSysUser(sysUser);
        return InvokeResponse.succ("新增用户成功");
    }

    @DeleteMapping("/user")
    public InvokeResponse deleteSysUsers(String ids) {
        sysUserService.deleteSysUsers(ids);
        return InvokeResponse.succ("删除成功");
    }

    @PutMapping("/user")
    public InvokeResponse update(@RequestBody SysUser sysUser) {
        sysUserService.updateSysUser(sysUser);
        return InvokeResponse.succ("更新成功");
    }
}
