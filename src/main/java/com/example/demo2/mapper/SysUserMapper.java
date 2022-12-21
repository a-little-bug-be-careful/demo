package com.example.demo2.mapper;

import com.example.demo2.domain.SysUser;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SysUserMapper {
    SysUser getSysUserById(Integer id);
    List<SysUser> getSysUserList(SysUser sysUser);
    int insertSysUser(SysUser sysUser);
    int deleteSysUserById(Integer id);
    int deleteSysUsers(String[] ids);
    int updateSysUser(SysUser sysUser);
    SysUser getSysUserByName(String userName);
    int checkExistSysUser(SysUser sysUser);
}
