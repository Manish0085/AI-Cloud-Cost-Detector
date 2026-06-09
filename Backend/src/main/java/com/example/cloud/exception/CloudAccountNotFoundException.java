package com.example.cloud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CloudAccountNotFoundException extends RuntimeException{

    public CloudAccountNotFoundException(String message) {
        super(message);
    }
}
