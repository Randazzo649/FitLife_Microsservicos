package com.fitlife.personal.infrastructure.config;

import com.fitlife.personal.application.port.in.PersonalUseCases;
import com.fitlife.personal.application.service.PersonalApplicationService;
import com.fitlife.personal.application.port.out.PersonalPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public PersonalUseCases personalUseCases(PersonalPersistencePort personais) {
        return new PersonalApplicationService(personais);
    }
}
