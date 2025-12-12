package com.wad3s.service_desk.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }

}
