package com.fitlife.usuario.infrastructure.adapter.in.web;

import com.fitlife.usuario.application.port.in.UsuarioUseCases;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioUseCases usuarios;

    public UsuarioController(UsuarioUseCases usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastrar(@RequestBody Map<String, Object> dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarios.cadastrar(dados));
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> credenciais) {
        return usuarios.login(credenciais);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public Map<String, Object> buscar(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {
        validarProprietario(id, jwt);
        return usuarios.buscar(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {
        validarProprietario(id, jwt);
        usuarios.excluir(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public Map<String, Object> atualizar(@PathVariable long id, @RequestBody Map<String, Object> dados, @AuthenticationPrincipal Jwt jwt) {
        validarProprietario(id, jwt);
        return usuarios.atualizarPerfil(id, dados);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/informacoes-medicas")
    public Map<String, Object> atualizarMedicas(@PathVariable long id, @RequestBody Map<String, Object> dados, @AuthenticationPrincipal Jwt jwt) {
        validarProprietario(id, jwt);
        return usuarios.atualizarInformacoesMedicas(id, dados);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}/foto", consumes = "multipart/form-data")
    public Map<String, Object> atualizarFoto(@PathVariable long id, @RequestPart("foto") MultipartFile foto, @AuthenticationPrincipal Jwt jwt) throws IOException {
        validarProprietario(id, jwt);
        return usuarios.atualizarFoto(id, foto.getBytes(), foto.getContentType() == null ? "" : foto.getContentType());
    }

    private void validarProprietario(long id, Jwt jwt) {
        if (jwt == null || !Long.toString(id).equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso permitido somente ao próprio usuário.");
        }
    }
}
