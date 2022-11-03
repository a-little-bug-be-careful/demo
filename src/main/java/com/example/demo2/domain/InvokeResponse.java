package com.example.demo2.domain;

public class InvokeResponse {
    private String code;
    private String msg;

    public InvokeResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public InvokeResponse() {

    }

    public static InvokeResponse succ(String msg) {
        return new InvokeResponse("1", msg);
    }

    public static InvokeResponse fail(String msg) {
        return new InvokeResponse("0", msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
