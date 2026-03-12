package com.uni.project.exception;

import org.springframework.http.HttpStatus;

public class ProductException extends ApiException {
    public ProductException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public ProductException(HttpStatus status, String message) {
        super(status, message);
    }
}
