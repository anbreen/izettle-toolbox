package com.izettle.gdpr.exception;

public class S3FailedException extends RuntimeException{

    public S3FailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
