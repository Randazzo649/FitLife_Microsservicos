package com.fitlife.usuario.application.service;

import com.fitlife.usuario.application.port.in.UsuarioUseCase;
import com.fitlife.usuario.application.port.out.SenhaEncoderPort;
import com.fitlife.usuario.application.port.out.UsuarioRepositoryPort;
import com.fitlife.usuario.domain.exception.EmailJaCadastradoException;
import com.fitlife.usuario.domain.exception.UsuarioNaoEncontradoException;
import com.fitlife.usuario.domain.model.Usuario;

import java.util.List;

public class UsuarioService implements UsuarioUseCase {
    private final UsuarioRepositoryPort usuarioRepository;
    private final SenhaEncoderPort senhaEncoder;

    public UsuarioService(UsuarioRepositoryPort usuarioRepository, SenhaEncoderPort senhaEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.senhaEncoder = senhaEncoder;
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public Usuario buscarPorId(long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado: " + id));
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado para o email informado."));
    }

    @Override
    public Usuario cadastrar(Usuario usuario) {
        usuarioRepository.buscarPorEmail(usuario.getEmail()).ifPresent(existente -> {
            throw new EmailJaCadastradoException("Ja existe um usuario cadastrado com este email.");
        });
        usuario.setSenha(senhaEncoder.codificar(usuario.getSenha()));
        return usuarioRepository.inserir(usuario);
    }

    @Override
    public Usuario atualizar(long id, Usuario usuario) {
        Usuario atual = buscarPorId(id);
        usuarioRepository.buscarPorEmail(usuario.getEmail()).ifPresent(existente -> {
            if (existente.getId() != id) {
                throw new EmailJaCadastradoException("Ja existe outro usuario cadastrado com este email.");
            }
        });
        usuario.setId(atual.getId());
        usuario.setSenha(senhaEncoder.codificar(usuario.getSenha()));
        if (!usuarioRepository.atualizar(usuario)) {
            throw new UsuarioNaoEncontradoException("Usuario nao encontrado: " + id);
        }
        return usuario;
    }

    @Override
    public void excluir(long id) {
        if (!usuarioRepository.deletarPorId(id)) {
            throw new UsuarioNaoEncontradoException("Usuario nao encontrado: " + id);
        }
    }
}
