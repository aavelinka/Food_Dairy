package com.uni.project.exception;

public class FailAfterUserException extends RuntimeException {
    public FailAfterUserException(String message) {
        super(message);
    }
}
