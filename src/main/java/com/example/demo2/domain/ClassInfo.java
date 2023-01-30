package com.example.demo2.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class ClassInfo {
    private String id;
    @Field(value = "classname")
    private String className;
    private Student students;
}
