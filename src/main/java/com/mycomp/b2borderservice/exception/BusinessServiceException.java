package com.mycomp.b2borderservice.exception;

public class BusinessServiceException extends RuntimeException {

    public BusinessServiceException() {
        super();
    }

    public BusinessServiceException(String message) {
        super(message);
    }
}
