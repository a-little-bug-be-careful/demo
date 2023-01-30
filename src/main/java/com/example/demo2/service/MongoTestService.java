package com.example.demo2.service;

import com.example.demo2.domain.ClassInfo;
import com.example.demo2.domain.MongoSysUser;
import com.example.demo2.domain.Student;
import java.util.List;

public interface MongoTestService {
    void insertUser(MongoSysUser sysUser);
    void deleteUser(MongoSysUser sysUser);
    void updateUser(MongoSysUser sysUser);
    List<MongoSysUser> getUser(MongoSysUser sysUser);
    List<ClassInfo> getStudentsByClassName(String className);
    List<Student> getStudents(String name);
    List<Student> getStudentsPage(MongoSysUser sysUser);
}
