package com.fitlife.usuario.infrastructure.adapter.in.web;

import com.fitlife.usuario.application.exception.ApplicationException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String,Object>> application(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("status",ex.getStatus(),"message",ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> invalid(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("status",400,"message",ex.getMessage()));
    }
}
