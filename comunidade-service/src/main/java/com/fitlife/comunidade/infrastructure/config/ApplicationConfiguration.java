package com.fitlife.comunidade.infrastructure.config;

import com.fitlife.comunidade.application.port.in.ComunidadeUseCases;
import com.fitlife.comunidade.application.service.ComunidadeApplicationService;
import com.fitlife.comunidade.application.port.out.ComunidadePersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public ComunidadeUseCases comunidadeUseCases(ComunidadePersistencePort comunidade) {
        return new ComunidadeApplicationService(comunidade);
    }
}
