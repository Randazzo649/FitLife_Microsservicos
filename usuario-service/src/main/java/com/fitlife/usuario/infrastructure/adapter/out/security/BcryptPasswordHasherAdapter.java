package com.fitlife.usuario.infrastructure.adapter.out.security;

import com.fitlife.usuario.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordHasherAdapter implements PasswordHasherPort {
    private final PasswordEncoder encoder;
    public BcryptPasswordHasherAdapter(PasswordEncoder encoder) { this.encoder = encoder; }
    @Override public String gerarHash(String senha) { return encoder.encode(senha); }
    @Override public boolean corresponde(String senha, String hash) { return encoder.matches(senha, hash); }
}
