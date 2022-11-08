package com.example.demo2.domain;

public class InvokeResponse<T> {
    private String code;
    private String msg;
    private T data;

    public InvokeResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public InvokeResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public InvokeResponse() {

    }

    public static InvokeResponse succ(String msg) {
        return new InvokeResponse("1", msg, null);
    }

    /**
     * 1.方法上的泛型，和类上的泛型不一定是相同的，可以不相同
     * 2.静态方法上不能访问类上的泛型，比如
     * public static InvokeResponse<T> succ(String msg, T t) {
     *         return new InvokeResponse("1", msg, t);
     *     }这个方法在编译时会报错，也很好理解，类上的泛型依赖于类的实例，而静态方法先于实例而存在，所以不能访问
     * 3.如下是一个泛型方法，访问修饰符public和方法返回值InvokeResponse之间的<T>用来标识方法是泛型方法；
     * InvokeResponse<T>中的<T>代表该方法返回的返回值InvokeResponse中的data类型
     **/
    public static <T> InvokeResponse<T> succ(String msg, T t) {
        return new InvokeResponse("1", msg, t);
    }

    public static <T> InvokeResponse<T> succ(T t){
        return new InvokeResponse<>("1", "succ", t);
    }

    public static InvokeResponse fail(String msg) {
        return new InvokeResponse("0", msg, null);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
