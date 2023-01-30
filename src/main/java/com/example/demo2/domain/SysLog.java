package com.example.demo2.domain;

import lombok.Data;
import java.util.Date;

@Data
public class SysLog extends Base{
    private Integer id;
    private String operator;
    private String operateType;
    private String content;
    private Date operateTime;
}
