package com.example.demo2.mapper;

import com.example.demo2.domain.Test;

public interface TestMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Test row);

    int insertSelective(Test row);
}