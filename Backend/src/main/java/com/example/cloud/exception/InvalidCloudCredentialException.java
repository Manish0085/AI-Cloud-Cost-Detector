package com.example.cloud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCloudCredentialException extends RuntimeException{

    public InvalidCloudCredentialException(String message) {
        super(message);
    }
}
