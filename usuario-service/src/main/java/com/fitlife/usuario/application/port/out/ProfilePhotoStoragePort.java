package com.fitlife.usuario.application.port.out;

import java.io.IOException;

public interface ProfilePhotoStoragePort {
    String salvar(long usuarioId, byte[] conteudo, String contentType) throws IOException;
}
