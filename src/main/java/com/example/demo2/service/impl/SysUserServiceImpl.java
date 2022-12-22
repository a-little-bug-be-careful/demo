package com.example.demo2.service.impl;

import com.example.demo2.domain.BusiException;
import com.example.demo2.domain.SysUser;
import com.example.demo2.mapper.SysUserMapper;
import com.example.demo2.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getSysUserById(Integer id) {
        if (null == id) {
            throw new BusiException("查询用户信息出错，用户id为空");
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
    @Transactional
    public int insertSysUser(SysUser sysUser) {
        if (StringUtils.isBlank(sysUser.getUserName())) {
            throw new BusiException("新增用户信息失败，用户名不能为空");
        }
        if (StringUtils.isBlank(sysUser.getPassWord())) {
            throw new BusiException("新增用户信息失败，密码不能为空");
        }
        SysUser oldSysUser = sysUserMapper.getSysUserByName(sysUser.getUserName());
        if (null != oldSysUser) {
            throw new BusiException("新增用户信息失败，用户名【" + sysUser.getUserName() + "】已存在");
        }
        //TODO 待替换成shiro
        sysUser.setCreateBy("admin");
        sysUser.setUpdateBy("admin");
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
        if (null == sysUser.getId()) {
            throw new BusiException("更新用户信息失败，用户id为空");
        }
        int result = sysUserMapper.updateSysUser(sysUser);
        if (result > 0) {
            return result;
        } else {
            throw new BusiException("更新用户信息失败，服务异常");
        }
    }

    @Override
    public int deleteSysUsers(String ids) {
        if (StringUtils.isBlank(ids)) {
            throw new BusiException("删除失败，用户id信息为空");
        }
        int result = sysUserMapper.deleteSysUsers(StringUtils.split(ids, ","));
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
