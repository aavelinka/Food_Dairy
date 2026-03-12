package com.uni.project.exception;

import org.springframework.http.HttpStatus;

public class MealException extends ApiException {
    public MealException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public MealException(HttpStatus status, String message) {
        super(status, message);
    }
}
