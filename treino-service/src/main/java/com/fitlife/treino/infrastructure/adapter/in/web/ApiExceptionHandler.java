package com.fitlife.treino.infrastructure.adapter.in.web;
import java.util.Map;
import com.fitlife.treino.application.service.TreinoNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,Object>> invalid(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("status",400,"message",ex.getMessage()));}
 @ExceptionHandler(TreinoNotFoundException.class) public ResponseEntity<Map<String,Object>> missing(TreinoNotFoundException ex){return ResponseEntity.status(404).body(Map.of("status",404,"message",ex.getMessage()));}
 @ExceptionHandler(Exception.class) public ResponseEntity<Map<String,Object>> general(Exception ex){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status",500,"message","Erro interno ao processar a solicitação."));}
}
