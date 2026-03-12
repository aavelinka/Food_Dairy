package com.uni.project.exception;

import org.springframework.http.HttpStatus;

public class UserException extends ApiException {
    public UserException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public UserException(HttpStatus status, String message) {
        super(status, message);
    }
}
