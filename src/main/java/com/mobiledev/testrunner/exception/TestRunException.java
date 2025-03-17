package com.mobiledev.testrunner.exception;

public class TestRunException extends RuntimeException {
    public TestRunException(String message) {
        super(message);
    }

    public TestRunException(String message, Throwable cause) {
        super(message, cause);
    }
}