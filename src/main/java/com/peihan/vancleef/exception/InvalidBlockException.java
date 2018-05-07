package com.peihan.vancleef.exception;

import com.peihan.vancleef.exception.base.ServiceException;

public class InvalidBlockException extends ServiceException{

    public InvalidBlockException(String message){
        super("INVALID_BLOCK",message);
    }
}
