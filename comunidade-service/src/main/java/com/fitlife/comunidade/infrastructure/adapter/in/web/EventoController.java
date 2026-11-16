package com.fitlife.comunidade.infrastructure.adapter.in.web;

import com.fitlife.comunidade.application.port.in.ComunidadeUseCases;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/comunidade")
public class EventoController {
    private final ComunidadeUseCases comunidade;
    public EventoController(ComunidadeUseCases comunidade){this.comunidade=comunidade;}
    @GetMapping("/eventos") public List<Map<String,Object>> listar(@RequestParam(required=false)Long usuarioId,@AuthenticationPrincipal Jwt jwt){
        long dono=usuarioAtual(jwt); validarSeInformado(usuarioId,dono); return comunidade.listar(dono);
    }
    @GetMapping("/eventos/usuario/{usuarioId}") public List<Map<String,Object>> meus(@PathVariable long usuarioId,@AuthenticationPrincipal Jwt jwt){
        validarProprietario(usuarioId,jwt); return comunidade.meus(usuarioId);
    }
    @PostMapping("/eventos") @ResponseStatus(HttpStatus.CREATED) public Map<String,Object> criar(@RequestBody Map<String,Object> dados,@AuthenticationPrincipal Jwt jwt){
        Map<String,Object> seguros=new LinkedHashMap<>(dados); seguros.put("usuarioId",usuarioAtual(jwt)); return comunidade.criar(seguros);
    }
    @PutMapping("/eventos/{id}") public Map<String,Object> atualizar(@PathVariable String id,@RequestBody Map<String,Object> dados,@AuthenticationPrincipal Jwt jwt){
        Map<String,Object> seguros=new LinkedHashMap<>(dados); seguros.put("usuarioId",usuarioAtual(jwt)); return comunidade.atualizar(id,seguros);
    }
    @PostMapping("/eventos/{id}/participacoes") public Map<String,Object> participar(@PathVariable String id,@RequestBody Map<String,Object> dados,@AuthenticationPrincipal Jwt jwt){
        long usuarioId=usuarioAtual(jwt); validarSeInformado(dados.get("usuarioId"),usuarioId); return comunidade.participar(id,usuarioId);
    }
    @GetMapping("/atividades") public List<Map<String,Object>> atividades(@RequestParam(required=false)Long usuarioId,@AuthenticationPrincipal Jwt jwt){
        long dono=usuarioAtual(jwt); validarSeInformado(usuarioId,dono); return comunidade.listarAtividades(dono);
    }
    private long usuarioAtual(Jwt jwt){
        if(jwt==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Autenticação necessária.");
        try{return Long.parseLong(jwt.getSubject());}catch(NumberFormatException ex){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token sem identificador de usuário válido.");}
    }
    private void validarProprietario(long usuarioId,Jwt jwt){if(usuarioId!=usuarioAtual(jwt))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Acesso permitido somente ao próprio usuário.");}
    private void validarSeInformado(Object informado,long atual){if(informado!=null){try{if(Long.parseLong(informado.toString())!=atual)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Usuário informado não corresponde ao token.");}catch(NumberFormatException ex){throw new IllegalArgumentException("Usuário não identificado.");}}}
}
