package com.uni.project.exception;

import org.springframework.http.HttpStatus;

public class NoteException extends ApiException {
    public NoteException(String message) {
        this(HttpStatus.NOT_FOUND, message);
    }

    public NoteException(HttpStatus status, String message) {
        super(status, message);
    }
}
