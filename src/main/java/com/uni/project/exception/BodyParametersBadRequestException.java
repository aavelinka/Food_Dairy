package com.uni.project.exception;

public class BodyParametersBadRequestException extends RuntimeException {
    public BodyParametersBadRequestException(String message) {
        super(message);
    }
}
