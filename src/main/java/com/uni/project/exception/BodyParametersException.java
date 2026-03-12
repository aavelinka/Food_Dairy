package com.uni.project.exception;

import org.springframework.http.HttpStatus;

public class BodyParametersException extends ApiException {
    public BodyParametersException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public BodyParametersException(HttpStatus status, String message) {
        super(status, message);
    }
}
