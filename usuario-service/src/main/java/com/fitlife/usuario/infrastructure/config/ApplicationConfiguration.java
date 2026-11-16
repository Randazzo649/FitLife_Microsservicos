package com.fitlife.usuario.infrastructure.config;

import com.fitlife.usuario.application.port.in.UsuarioUseCases;
import com.fitlife.usuario.application.service.UsuarioApplicationService;
import com.fitlife.usuario.application.port.out.UsuarioPersistencePort;
import com.fitlife.usuario.application.port.out.PasswordHasherPort;
import com.fitlife.usuario.application.port.out.JwtTokenPort;
import com.fitlife.usuario.application.port.out.ProfilePhotoStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public UsuarioUseCases usuarioUseCases(UsuarioPersistencePort usuarios, PasswordHasherPort senhas, JwtTokenPort tokens, ProfilePhotoStoragePort fotos) {
        return new UsuarioApplicationService(usuarios, senhas, tokens, fotos);
    }
}
