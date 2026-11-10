# Treino Service

Estrutura inicial independente para o microsservico de treinos do FitLife. Utiliza Spring Boot e possui a dependencia Spring Data MongoDB, com configuracao por `MONGODB_URI`.

A estrutura segue Ports and Adapters. O modelo de treino, os endpoints CRUD, os eventos do Azure Service Bus e os testes unitarios ainda precisam ser definidos pelo grupo conforme os requisitos funcionais do FitLife. Esta base nao cria campos ou regras de negocio presumidos.

## Executar

Configure `MONGODB_URI` conforme `.env.example` e execute:

```bash
mvn spring-boot:run
```

A porta padrao e 8082. Swagger: `http://localhost:8082/swagger-ui.html`.
