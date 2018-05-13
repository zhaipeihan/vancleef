package com.peihan.vancleef.exception;

import com.peihan.vancleef.exception.base.ServiceException;

public class VerifyFailedException extends ServiceException {

    public VerifyFailedException(String message){
        super("VERIFY_FAILED",message);
    }
}
