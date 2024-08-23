package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        System.out.println("-------------------------------------");

        System.out.println("Hata: " + ex.getMessage());
        return new ResponseEntity<>("Bir hata meydana geldi: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
