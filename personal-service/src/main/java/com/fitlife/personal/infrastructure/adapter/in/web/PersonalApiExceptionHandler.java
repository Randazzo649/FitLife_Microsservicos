package com.fitlife.personal.infrastructure.adapter.in.web;

import com.fitlife.personal.application.service.PersonalNotFoundException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PersonalApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> invalid(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("status", 400, "message", ex.getMessage()));
    }

    @ExceptionHandler(PersonalNotFoundException.class)
    public ResponseEntity<Map<String,Object>> missing(PersonalNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("status", 404, "message", ex.getMessage()));
    }
}
