package com.fitlife.usuario.application.port.in;

import com.fitlife.usuario.domain.model.Usuario;
import java.util.List;

public interface UsuarioUseCase {
    List<Usuario> listar();
    Usuario buscarPorId(long id);
    Usuario buscarPorEmail(String email);
    Usuario cadastrar(Usuario usuario);
    Usuario atualizar(long id, Usuario usuario);
    void excluir(long id);
}
