package com.substring.auth.auth_app.exception;

import com.substring.auth.auth_app.dtos.ErrorResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //resource not found exception handler :: method
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
       ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(),404);
        return ResponseEntity.status(404).body(errorResponse);



    }
}