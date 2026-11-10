package com.fitlife.usuario.adapters.in.web.dto;

import com.fitlife.usuario.domain.model.Usuario;

public record UsuarioResponse(
        long id,
        String nome,
        String data_nasc,
        String cpf,
        String genero,
        String telefone,
        String endereco,
        String email
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getData_nasc(),
                usuario.getCpf(),
                usuario.getGenero(),
                usuario.getTelefone(),
                usuario.getEndereco(),
                usuario.getEmail()
        );
    }
}
