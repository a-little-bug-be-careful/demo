package com.example.demo2.domain;

public class BusiException extends RuntimeException{

    public BusiException(String errorMessage){
        super(errorMessage);
    }

    @Override
    public String toString() {
        return "BusiException{" +
                ", message=" + this.getMessage() +
                '}';
    }

}
