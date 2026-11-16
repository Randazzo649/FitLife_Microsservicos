package com.fitlife.usuario.application.port.out;

public interface PasswordHasherPort {
    String gerarHash(String senha);
    boolean corresponde(String senha, String hash);
}
