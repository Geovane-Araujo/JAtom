package com.jatom.exceptions;

public class ServiceException extends RuntimeException{

    String category;

    String errorCode;

    public ServiceException(String errorCode, String message, Throwable cause){
        super(message,cause);
        this.errorCode = errorCode;
    }

    public ServiceException(String category,String errorCode, String message, Throwable cause){
        super(message,cause);
        this.errorCode = errorCode;
        this.category = category;
    }
}
