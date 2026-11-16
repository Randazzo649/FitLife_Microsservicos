package com.fitlife.usuario.application.port.in;

import java.io.IOException;
import java.util.Map;

public interface UsuarioUseCases {
    Map<String, Object> cadastrar(Map<String, Object> dados);
    Map<String, Object> login(Map<String, Object> credenciais);
    Map<String, Object> buscar(long id);
    void excluir(long id);
    Map<String, Object> atualizarPerfil(long id, Map<String, Object> dados);
    Map<String, Object> atualizarInformacoesMedicas(long id, Map<String, Object> dados);
    Map<String, Object> atualizarFoto(long id, byte[] conteudo, String contentType) throws IOException;
}
