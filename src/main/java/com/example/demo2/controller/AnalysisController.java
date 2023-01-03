package com.example.demo2.controller;

import com.example.demo2.domain.Analysis;
import com.example.demo2.domain.InvokeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @GetMapping("/user")
    public InvokeResponse analyseUser(Analysis analysis) {
        if (StringUtils.isBlank(analysis.getBeginDate())) {
            return InvokeResponse.fail("开始日期不能为空");
        }
        if (StringUtils.isBlank(analysis.getEndDate())) {
            return InvokeResponse.fail("截止日期不能为空");
        }
        if (StringUtils.isBlank(analysis.getType())) {
            return InvokeResponse.fail("统计类型不能为空");
        }
        //TODO 测试数据
        Map<String, List> map = new HashMap<>();
        map.put("xAxis", Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("series", Arrays.asList(1200, 1320, 1010, 1340, 900, 2300, 2100));
        return InvokeResponse.succ(map);
    }
}
