package com.coral.test.spring.natives.core.exception;

/**
 * OperationNotSupportedException
 * 
 * @author huss
 */
public class OperationNotSupportedException extends RuntimeException {

    public OperationNotSupportedException() {
        super("this operation is not supported");
    }

    public OperationNotSupportedException(String message) {
        super(message);
    }

    public OperationNotSupportedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}