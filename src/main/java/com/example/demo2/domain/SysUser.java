package com.example.demo2.domain;

import lombok.Data;
import java.util.Date;

@Data
public class SysUser extends Base{
    private Integer id;
    private String ids;
    private String userName;
    private String passWord;
    private String loginIp;
    private String status;
    private String updateBy;
    private String createBy;
    private Date createTime;
    private Date updateTime;
    private String role;
}
