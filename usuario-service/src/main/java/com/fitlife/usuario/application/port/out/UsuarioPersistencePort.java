package com.fitlife.usuario.application.port.out;

import com.fitlife.usuario.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioPersistencePort {
    long proximoId();
    Optional<Usuario> buscarPorId(long id);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existeCpf(String cpf);
    Usuario salvar(Usuario usuario);
    boolean excluir(long id);
}
