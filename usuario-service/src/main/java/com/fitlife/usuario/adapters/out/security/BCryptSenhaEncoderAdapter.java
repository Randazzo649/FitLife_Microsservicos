package com.fitlife.usuario.adapters.out.security;

import com.fitlife.usuario.application.port.out.SenhaEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptSenhaEncoderAdapter implements SenhaEncoderPort {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String codificar(String senha) {
        return encoder.encode(senha);
    }
}
