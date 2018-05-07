package com.peihan.vancleef.exception;

import com.peihan.vancleef.exception.base.ServiceException;

public class OperateFailedException extends ServiceException {

    public OperateFailedException(String message) {
        super("OPERATE_FAILED", message);
    }

}
