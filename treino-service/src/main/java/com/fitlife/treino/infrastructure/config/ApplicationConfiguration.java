package com.fitlife.treino.infrastructure.config;

import com.fitlife.treino.application.port.in.TreinoUseCases;
import com.fitlife.treino.application.service.TreinoApplicationService;
import com.fitlife.treino.application.port.out.TreinoPersistencePort;
import com.fitlife.treino.application.port.out.TreinoEventPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public TreinoUseCases treinoUseCases(TreinoPersistencePort treinos, TreinoEventPublisherPort eventos) {
        return new TreinoApplicationService(treinos, eventos);
    }
}
