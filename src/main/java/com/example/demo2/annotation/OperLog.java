package com.example.demo2.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
/**
 * 该注解可以用于类、接口、枚举、注解
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * RetentionPolicy.SOURCE	表示注解只保留在源文件，当java文件编译成class文件，就会消失	源文件	只是做一些检查性的操作，，比如 @Override 和 @SuppressWarnings
 * RetentionPolicy.CLASS	注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期	class文件（默认）	要在编译时进行一些预处理操作，比如生成一些辅助代码（如 ButterKnife）
 * RetentionPolicy.RUNTIME	注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在	运行时也存在	需要在运行时去动态获取注解信息
 */
public @interface OperLog {

    /**
     * 功能
     * @return
     */
    String title() default "";

    /**
     * 操作信息
     * @return
     */
    String msg() default "";
}
