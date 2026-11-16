package com.fitlife.comunidade.infrastructure.adapter.in.web;
import com.fitlife.comunidade.application.service.EventoForbiddenException;
import com.fitlife.comunidade.application.service.EventoNotFoundException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,Object>> invalid(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("status",400,"message",ex.getMessage()));}
 @ExceptionHandler(EventoNotFoundException.class) public ResponseEntity<Map<String,Object>> missing(EventoNotFoundException ex){return ResponseEntity.status(404).body(Map.of("status",404,"message",ex.getMessage()));}
 @ExceptionHandler(EventoForbiddenException.class) public ResponseEntity<Map<String,Object>> forbidden(EventoForbiddenException ex){return ResponseEntity.status(403).body(Map.of("status",403,"message",ex.getMessage()));}
}
