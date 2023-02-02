package com.example.demo2.service.impl;

import com.example.demo2.domain.*;
import com.example.demo2.service.MongoTestService;
import com.example.demo2.util.FileUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MongoTestServiceImpl implements MongoTestService {

    private Logger logger = LoggerFactory.getLogger(MongoTestServiceImpl.class);

    /**
     * MongoDB的使用MongoTemplate操作增删改查，分页，排序（含内嵌数据）
     * https://www.codenong.com/cs106854254/
     */

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 简单插入数据
     * @param sysUser
     */
    @Override
    public void insertUser(MongoSysUser sysUser) {
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        MongoSysUser saveUser = mongoTemplate.save(sysUser, "sys_user");
        logger.info("saveUser:{}", saveUser);
    }

    /**
     * 简单删除数据
     * @param sysUser
     */
    @Override
    public void deleteUser(MongoSysUser sysUser) {
        Criteria criteria = Criteria.where("user_name").is(sysUser.getUserName());
        DeleteResult deleteResult = mongoTemplate.remove(new Query(criteria), MongoSysUser.class, "sys_user");
        logger.info("deleteResult:{}", deleteResult);
    }

    /**
     * 简单更新数据
     * @param sysUser
     */
    @Override
    public void updateUser(MongoSysUser sysUser) {
        Update update = new Update();
        update.set("create_time", new Date());
        update.set("update_time", new Date());
        UpdateResult updateResult = mongoTemplate.updateFirst(new Query(Criteria.where("user_name").is(sysUser.getUserName())), update, "sys_user");
        logger.info("saveUser:{}", updateResult);
    }

    /**
     * 简单查询
     * @param sysUser
     * @return
     */
    @Override
    public List<MongoSysUser> getUser(MongoSysUser sysUser) {
        List<MongoSysUser> sysUsers = mongoTemplate.findAll(MongoSysUser.class, "sys_user");
        return sysUsers;
    }

    /**
     * 关联查询
     * @param className
     * @return
     */
    @Override
    public List<ClassInfo> getStudentsByClassName(String className) {
        Criteria criteria = Criteria.where("classname").is(className);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.project().and(ConvertOperators.Convert.convertValueOf("_id").to("string")).as("id"),
                Aggregation.lookup("student", "id", "class_id", "students"),
                Aggregation.unwind("students"));
        AggregationResults<ClassInfo> aggregationResults = mongoTemplate.aggregate(aggregation, "class", ClassInfo.class);
        List<ClassInfo> classInfos = aggregationResults.getMappedResults();
        return classInfos;
    }

    /**
     * 简单带条件查询
     * @param name
     * @return
     */
    @Override
    public List<Student> getStudents(String name) {
        List<Student> students = mongoTemplate.find(new Query(Criteria.where("name").is(name)), Student.class, "student");
        return students;
    }

    /**
     * 简单分页查询
     * @param sysUser
     * @return
     */
    @Override
    public List<Student> getStudentsPage(MongoSysUser sysUser) {
        //倒序
        Sort sort = Sort.by("_id").descending();
        //分页  当前页+页的大小（当前页应该是实际页数减一）
        PageRequest pageRequest = PageRequest.of(sysUser.getPageNo(), sysUser.getPageSize());
        Query query = new Query();
        query.with(sort);
        query.with(pageRequest);
        List<Student> students = mongoTemplate.find(query, Student.class, "student");
        return students;
    }

    /**
     * 保存文件
     * @param mongoFile
     */
    @Override
    public void saveFile(MongoFile mongoFile) {
        File file = new File(mongoFile.getPath());
        mongoFile = new MongoFile("test", "jpg", file.length(), new Binary(FileUtil.fileToBytes(file, "")));
        mongoFile.setId(UUID.randomUUID().toString());
        MongoFile saveFile = mongoTemplate.save(mongoFile);
        logger.info("saveFile:{}", saveFile);
    }

    /**
     * 读取文件
     * @param mongoFile
     * @return
     */
    @Override
    public MongoFile getFile(MongoFile mongoFile) {
        MongoFile file = mongoTemplate.findById(mongoFile.getId(), MongoFile.class, "mongoFile");
        return file;
    }

    @Override
    public MongoSysUser getHeaderImage(MongoSysUser mongoSysUser) {
        List<MongoSysUser> mongoSysUsers = mongoTemplate.find(new Query(Criteria.where("user_name").is(mongoSysUser.getUserName())),
                MongoSysUser.class, "mongoSysUser");
        return mongoSysUsers.iterator().next();
    }
}
