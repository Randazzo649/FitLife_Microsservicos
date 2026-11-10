package com.fitlife.usuario.config;

import com.fitlife.usuario.application.port.in.UsuarioUseCase;
import com.fitlife.usuario.application.port.out.SenhaEncoderPort;
import com.fitlife.usuario.application.port.out.UsuarioRepositoryPort;
import com.fitlife.usuario.application.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepositoryPort usuarioRepository, SenhaEncoderPort senhaEncoder) {
        return new UsuarioService(usuarioRepository, senhaEncoder);
    }
}
