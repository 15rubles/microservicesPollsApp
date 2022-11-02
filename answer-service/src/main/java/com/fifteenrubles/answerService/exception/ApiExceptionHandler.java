package com.fifteenrubles.answerService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleRequestException(ApiRequestException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getHttpStatus());
        return new ResponseEntity<>(apiException,  e.getHttpStatus());
    }
   @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
