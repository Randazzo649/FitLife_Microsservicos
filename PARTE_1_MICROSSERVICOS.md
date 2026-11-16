# FitLife — Entrega por partes

## Parte 1: Microsserviços e persistência

Este pacote contém exclusivamente o repositório de microsserviços e os artefatos de configuração dos bancos de dados. Não inclui o API Gateway, os BFFs nem os Microfrontends; esses componentes serão tratados nas próximas partes.

Quatro aplicações independentes em Java 21 / Spring Boot:

- `usuario-service`: MongoDB / Cosmos DB for MongoDB;
- `treino-service`: Azure SQL Database;
- `personal-service`: Azure SQL Database próprio;
- `comunidade-service`: Azure SQL Database próprio.

A arquitetura adotada é Hexagonal (Ports and Adapters), com separação `domain`, `application` e `infrastructure`. Há testes unitários (JUnit 5/Mockito) e regras arquiteturais (ArchUnit) em cada microsserviço.

Leia `README.md` para execução, endpoints e configuração Azure; leia `database/README.md` para a preparação dos bancos.

**Validação:** os arquivos POM XML e `application.yml` foram verificados estruturalmente. A suíte Maven não foi executada neste ambiente porque Maven e acesso ao Maven Central não estão disponíveis; execute `mvn -B test` na raiz do repositório ou use o workflow de CI antes de considerar os testes aprovados.
