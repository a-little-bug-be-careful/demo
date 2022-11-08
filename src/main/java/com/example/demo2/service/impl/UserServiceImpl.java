package com.example.demo2.service.impl;

import com.example.demo2.domain.InvokeResponse;
import com.example.demo2.domain.User;
import com.example.demo2.mapper.UserMapper;
import com.example.demo2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Value("${server.port}")
    private String port;

    @Override
    public InvokeResponse selectUser(Integer id) {

        LocalDateTime localDateTime = LocalDateTime.now();
        StringBuffer sb = new StringBuffer();
        List<User> users = this.userMapper.selectUser(id);
        if (users.isEmpty()) {
            return InvokeResponse.fail("未查询到id为【"+ id + "】的用户信息");
        }
        sb.append("hello ").append(users.iterator().next().getName());
        sb.append("恭喜你成功用docker部署运行了jar包   ");
        sb.append("当前系统时间：").append(localDateTime.toString()).append("   ");
        sb.append("容器内运行端口号：").append(port);
        return InvokeResponse.succ(sb.toString());
    }

    @Override
    public InvokeResponse insertUser(User user) {
        int i = this.userMapper.insertUser(user);
        if (i > 0) {
            return InvokeResponse.succ("新增用户成功");
        }
        return InvokeResponse.fail("新增用户失败");
    }

    @Override
    public InvokeResponse deleteUser(Integer id) {
        List<User> users = this.userMapper.selectUser(id);
        if (users.isEmpty()) {
            return InvokeResponse.fail("删除用户失败：未查询到用户信息");
        }
        int i = this.userMapper.deleteUser(id);
        if (i > 0) {
            return InvokeResponse.succ("删除用户成功");
        }
        return InvokeResponse.fail("删除用户失败");
    }

    @Override
    public InvokeResponse editUser(User user) {
        List<User> users = this.userMapper.selectUser(user.getId());
        if (users.isEmpty()) {
            return InvokeResponse.fail("更新用户失败：未查询到用户信息");
        }
        int i = this.userMapper.editUser(user);
        if (i > 0) {
            return InvokeResponse.succ("更新用户信息成功");
        }
        return InvokeResponse.fail("更新用户信息失败");
    }
}
