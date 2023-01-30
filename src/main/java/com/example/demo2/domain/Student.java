package com.example.demo2.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Student {
    private String id;
    private String name;
    @Field(value = "class")
    private String className;
    @Field(value = "class_id")
    private String classId;
}
