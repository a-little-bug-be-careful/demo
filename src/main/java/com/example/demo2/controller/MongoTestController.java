package com.example.demo2.controller;

import com.example.demo2.config.NacosConfigCustomerProperties;
import com.example.demo2.domain.*;
import com.example.demo2.service.MongoTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mongo")
@RefreshScope
public class MongoTestController {

    @Autowired
    private MongoTestService mongoTestService;

    @Autowired
    private NacosConfigCustomerProperties nacosConfigCustomerProperties;

    @GetMapping("/user")
    public InvokeResponse getUser(MongoSysUser sysUser) {
        System.out.println(nacosConfigCustomerProperties.getImagePath());
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

    @PostMapping("file")
    public InvokeResponse saveMongoFile(MongoFile mongoFile) {
        mongoTestService.saveFile(mongoFile);
        return InvokeResponse.succ();
    }

    @GetMapping("/file")
    public InvokeResponse getMongoFile(MongoFile mongoFile, HttpServletResponse response) throws IOException {
        MongoFile file = mongoTestService.getFile(mongoFile);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        try {
            byte[] bytes = file.getContent().getData();
            servletOutputStream.write(bytes);
        } catch (Exception e) {
        } finally {
            servletOutputStream.close();
        }
        return InvokeResponse.succ();
    }

    @GetMapping("/image")
    public InvokeResponse getHeaderImage(MongoSysUser mongoSysUser, HttpServletResponse response) throws IOException {
        MongoSysUser sysUser = mongoTestService.getHeaderImage(mongoSysUser);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        try {
            byte[] bytes = sysUser.getImage().getData();
            servletOutputStream.write(bytes);
        } catch (Exception e) {
        } finally {
            servletOutputStream.close();
        }
        return InvokeResponse.succ();
    }
}
