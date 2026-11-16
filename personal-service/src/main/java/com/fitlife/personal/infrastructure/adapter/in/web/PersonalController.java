package com.fitlife.personal.infrastructure.adapter.in.web;

import com.fitlife.personal.application.port.in.PersonalUseCases;
import com.fitlife.personal.application.service.PersonalNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/personais")
public class PersonalController {
    private final PersonalUseCases personais;
    public PersonalController(PersonalUseCases personais){this.personais=personais;}
    @GetMapping public List<Map<String,Object>> listar(@RequestParam(required=false)String modalidade,@RequestParam(required=false)String cidade){return personais.listar(modalidade,cidade);}
    @PostMapping("/{id}/contatos")
    public ResponseEntity<Map<String,Object>> contato(@PathVariable long id,@RequestBody Map<String,Object> dados,@AuthenticationPrincipal Jwt jwt){
        Object raw=dados.get("usuarioId");
        if(raw==null) return ResponseEntity.badRequest().body(Map.of("status",400,"message","Usuário inválido."));
        try{
            long usuarioId=Long.parseLong(raw.toString());
            if(jwt==null || !Long.toString(usuarioId).equals(jwt.getSubject())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status",403,"message","Usuário inválido para este token."));
            return ResponseEntity.ok(personais.registrarContato(id,usuarioId));
        }
        catch(NumberFormatException ex){return ResponseEntity.badRequest().body(Map.of("status",400,"message","Usuário inválido."));}
        catch(PersonalNotFoundException ex){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status",404,"message",ex.getMessage()));}
    }
}
