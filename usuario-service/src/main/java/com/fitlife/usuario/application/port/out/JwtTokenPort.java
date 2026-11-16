package com.fitlife.usuario.application.port.out;

import java.util.Map;

public interface JwtTokenPort {
    String emitir(long usuarioId, String email);
    Map<String, Object> jwks();
}
