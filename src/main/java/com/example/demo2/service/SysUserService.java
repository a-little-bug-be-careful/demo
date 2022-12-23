package com.example.demo2.service;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.SysUser;
import java.util.List;

public interface SysUserService {
    SysUser getSysUserById(Integer id);
    List<SysUser> getSysUserList(SysUser sysUser);
    int insertSysUser(SysUser sysUser);
    int deleteSysUserById(Integer id);
    int deleteSysUsers(String ids);
    int updateSysUser(SysUser sysUser);
    SysUser getSysUserByName(String userName);
    int checkExistSysUser(SysUser sysUser);
    int checkExistSysUserByUserName(SysUser sysUser);
    InvokeResponse updateSysUserByUserName(SysUser sysUser);
    InvokeResponse resetPassWord(SysUser sysUser);
}
