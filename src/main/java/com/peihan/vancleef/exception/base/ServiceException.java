package com.peihan.vancleef.exception.base;

public class ServiceException extends Exception {
    private String code;

    public ServiceException(String code, String message) {
        super(message);
        setCode(code);
    }

    public ServiceException(String message) {
        this("USER_EXC", message);
    }

    private void setCode(String code) {
        this.code = code;
    }


}
