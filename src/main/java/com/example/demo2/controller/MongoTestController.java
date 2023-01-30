package com.example.demo2.controller;

import com.example.demo2.domain.*;
import com.example.demo2.service.MongoTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/mongo")
@RefreshScope
public class MongoTestController {
    @Value("${database}")
    private String database;

    @Value("${authenticationDatabase}")
    private String authentication;

    @Value("${uri}")
    private String uri;

    @Autowired
    private MongoTestService mongoTestService;

    @GetMapping("/user")
    public InvokeResponse getUser(MongoSysUser sysUser) {
        System.out.println(database + ";" + authentication + ";" + uri);
        List<MongoSysUser> sysUsers = mongoTestService.getUser(sysUser);
        return InvokeResponse.succ(sysUsers);
    }

    @GetMapping("/class/students")
    public InvokeResponse getStudents(String className) {
        List<ClassInfo> students = mongoTestService.getStudentsByClassName(className);
        return InvokeResponse.succ(students);
    }

    @GetMapping("/student")
    public InvokeResponse getStudent(String name) {
        List<Student> students = mongoTestService.getStudents(name);
        return InvokeResponse.succ(students);
    }

    @DeleteMapping("/user")
    public InvokeResponse deleteUser(MongoSysUser sysUser) {
        mongoTestService.deleteUser(sysUser);
        return InvokeResponse.succ();
    }

    @PutMapping("/user")
    public InvokeResponse updateUser(MongoSysUser sysUser) {
        mongoTestService.updateUser(sysUser);
        return InvokeResponse.succ();
    }

    @PostMapping("/user")
    public InvokeResponse insertUser(MongoSysUser sysUser) {
        mongoTestService.insertUser(sysUser);
        return InvokeResponse.succ();
    }

    @GetMapping("/page/students")
    public InvokeResponse getStudentsPage(MongoSysUser sysUser) {
        List<Student> students = mongoTestService.getStudentsPage(sysUser);
        return InvokeResponse.succ(students);
    }
}
