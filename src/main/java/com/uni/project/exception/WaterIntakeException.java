package com.uni.project.exception;

import org.springframework.http.HttpStatus;

public class WaterIntakeException extends ApiException {
    public WaterIntakeException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public WaterIntakeException(HttpStatus status, String message) {
        super(status, message);
    }
}
