package com.peihan.vancleef.exception.base;

public class SystemException extends RuntimeException {

    private String code;

    public SystemException(String code, String message) {
        super(message);
        setCode(code);
    }

    public SystemException(String message) {
        this("SYS_EXC", message);
    }

    private void setCode(String code) {
        this.code = code;
    }


}
