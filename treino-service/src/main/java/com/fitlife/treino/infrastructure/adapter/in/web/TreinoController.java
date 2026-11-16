package com.fitlife.treino.infrastructure.adapter.in.web;

import com.fitlife.treino.application.port.in.TreinoUseCases;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {
    private final TreinoUseCases treinos;
    public TreinoController(TreinoUseCases treinos){this.treinos=treinos;}
    @GetMapping("/usuario/{usuarioId}") public List<Map<String,Object>> listar(@PathVariable long usuarioId,@AuthenticationPrincipal Jwt jwt){validarProprietario(usuarioId,jwt);return treinos.listar(usuarioId);}
    @GetMapping("/usuario/{usuarioId}/{treinoId}") public Map<String,Object> buscar(@PathVariable long usuarioId,@PathVariable long treinoId,@AuthenticationPrincipal Jwt jwt){validarProprietario(usuarioId,jwt);return treinos.buscar(usuarioId,treinoId);}
    @DeleteMapping("/usuario/{usuarioId}/{treinoId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long usuarioId,@PathVariable long treinoId,@AuthenticationPrincipal Jwt jwt){validarProprietario(usuarioId,jwt);treinos.excluir(usuarioId,treinoId);}
    @PostMapping("/usuario/{usuarioId}") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> criar(@PathVariable long usuarioId,@RequestBody Map<String,Object> dados,@AuthenticationPrincipal Jwt jwt){validarProprietario(usuarioId,jwt);return treinos.criar(usuarioId,dados);}
    @PutMapping("/usuario/{usuarioId}")
    public List<Map<String,Object>> substituir(@PathVariable long usuarioId,@RequestBody List<Map<String,Object>> dados,@AuthenticationPrincipal Jwt jwt){validarProprietario(usuarioId,jwt);return treinos.substituir(usuarioId,dados);}

    private void validarProprietario(long usuarioId, Jwt jwt) {
        if (jwt == null || !Long.toString(usuarioId).equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso permitido somente ao próprio usuário.");
        }
    }
}
