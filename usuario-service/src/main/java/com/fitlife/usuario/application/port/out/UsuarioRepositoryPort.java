package com.fitlife.usuario.application.port.out;

import com.fitlife.usuario.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    List<Usuario> listarTodos();
    Optional<Usuario> buscarPorId(long id);
    Optional<Usuario> buscarPorEmail(String email);
    Usuario inserir(Usuario usuario);
    boolean atualizar(Usuario usuario);
    boolean deletarPorId(long id);
}
