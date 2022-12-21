package com.example.demo2.service;

import com.example.demo2.domain.BusiException;
import com.example.demo2.domain.SysUser;
import com.example.demo2.mapper.SysUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getSysUserById(Integer id) {
        if (null == id) {
            throw new BusiException("获取用户信息失败，参数错误");
        }
        SysUser sysUser = sysUserMapper.getSysUserById(id);
        return sysUser;
    }

    @Override
    public List<SysUser> getSysUserList(SysUser sysUser) {
        List<SysUser> sysUsers = sysUserMapper.getSysUserList(sysUser);
        return sysUsers;
    }

    @Override
    public int insertSysUser(SysUser sysUser) {
        if (StringUtils.isBlank(sysUser.getUserName())) {
            throw new BusiException("新增用户信息失败，用户名不能为空");
        }
        sysUser = sysUserMapper.getSysUserByName(sysUser.getUserName());
        if (null != sysUser) {
            throw new BusiException("新增用户信息失败，用户名【" + sysUser.getUserName() + "】已存在");
        }
        int result = sysUserMapper.insertSysUser(sysUser);
        if (result > 0) {
            return result;
        } else {
            throw new BusiException("新增用户信息失败，服务异常");
        }
    }

    @Override
    public int deleteSysUserById(Integer id) {
        if (null == id) {
            throw new BusiException("删除用户信息失败，用户id为空");
        }
        int result = sysUserMapper.deleteSysUserById(id);
        if (result > 0) {
            return result;
        } else {
            throw new BusiException("删除用户信息失败，服务异常");
        }
    }

    @Override
    public int updateSysUser(SysUser sysUser) {
        int result = sysUserMapper.updateSysUser(sysUser);
        if (result > 0) {
            return result;
        } else {
            throw new BusiException("更新用户信息失败，服务异常");
        }
    }

    @Override
    public int deleteSysUsers(SysUser sysUser) {
        if (StringUtils.isBlank(sysUser.getIds())) {
            throw new BusiException("删除用户信息失败，用户id为空");
        }
        int result = sysUserMapper.deleteSysUsers(StringUtils.split(sysUser.getIds(), ","));
        if (result > 0) {
            return result;
        } else {
            throw new BusiException("删除用户信息失败，服务异常");
        }
    }

    @Override
    public SysUser getSysUserByName(String userName) {
        SysUser sysUser = sysUserMapper.getSysUserByName(userName);
        return sysUser;
    }

    @Override
    public int checkExistSysUser(SysUser sysUser) {
        return sysUserMapper.checkExistSysUser(sysUser);
    }
}
