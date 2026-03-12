package com.uni.project.exception;

public class NoteConflictException extends RuntimeException {
    public NoteConflictException(String message) {
        super(message);
    }
}
