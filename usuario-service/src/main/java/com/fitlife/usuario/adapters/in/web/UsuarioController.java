package com.fitlife.usuario.adapters.in.web;

import com.fitlife.usuario.adapters.in.web.dto.UsuarioRequest;
import com.fitlife.usuario.adapters.in.web.dto.UsuarioResponse;
import com.fitlife.usuario.application.port.in.UsuarioUseCase;
import com.fitlife.usuario.domain.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioUseCase usuarioUseCase;

    public UsuarioController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioUseCase.listar().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable long id) {
        return UsuarioResponse.from(usuarioUseCase.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public UsuarioResponse buscarPorEmail(@RequestParam String email) {
        return UsuarioResponse.from(usuarioUseCase.buscarPorEmail(email));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioUseCase.cadastrar(toModel(request));
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuario.getId()))
                .body(UsuarioResponse.from(usuario));
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable long id, @Valid @RequestBody UsuarioRequest request) {
        return UsuarioResponse.from(usuarioUseCase.atualizar(id, toModel(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id) {
        usuarioUseCase.excluir(id);
    }

    private Usuario toModel(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setData_nasc(request.data_nasc());
        usuario.setCpf(request.cpf());
        usuario.setGenero(request.genero());
        usuario.setTelefone(request.telefone());
        usuario.setEndereco(request.endereco());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        return usuario;
    }
}
