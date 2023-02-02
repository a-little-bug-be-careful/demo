package com.example.demo2.domain;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

@Data
@Document
public class MongoSysUser extends Base{
    private String id;
    private String ids;
    @Field(value = "user_name")
    private String userName;
    @Field(value = "pass_word")
    private String passWord;
    @Field(value = "login_ip")
    private String loginIp;
    private String status;
    @Field(value = "update_by")
    private String updateBy;
    @Field(value = "create_by")
    private String createBy;
    @Field(value = "create_time")
    private Date createTime;
    @Field(value = "update_time")
    private Date updateTime;
    private String role;
    private Binary image;
}
