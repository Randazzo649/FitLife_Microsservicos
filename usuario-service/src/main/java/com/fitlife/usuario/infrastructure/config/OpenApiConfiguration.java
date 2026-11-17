package com.fitlife.usuario.infrastructure.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FitLife Usuários API",
                version = "2.0.0",
                description = "Cadastro, autenticação e gestão do perfil do usuário."
        ),
        servers = {
            @Server(
                    url = "https://fitlifeusuarioservice-gbc5bdb6fccxc2e2.mexicocentral-01.azurewebsites.net",
                    description = "Usuario Service"
            ),
            @Server(
                    url = "https://fitlifeapigateway-g0enb0dsc2hhe7cp.mexicocentral-01.azurewebsites.net",
                    description = "API Gateway"
            )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfiguration {
}
