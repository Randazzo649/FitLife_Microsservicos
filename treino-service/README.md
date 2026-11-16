# treino-service — Azure SQL + Azure Service Bus

## Resumo
Serviço responsável por CRUD dos planos de treino. Persistência própria em Azure SQL Database (`FitLife_Treinos`). Publica eventos quando um treino ou plano é criado/atualizado.

## Arquitetura adotada
Arquitetura Hexagonal. O Controller chama a porta de entrada; o caso de uso depende de uma porta de persistência e de uma porta de publicação de eventos; os adaptadores implementam SQL Server e Azure Service Bus.

## Arc42
1. **Introdução e objetivos:** manter sessões, categorias e exercícios de cada usuário.
2. **Restrições:** Java 21, Spring Boot 3.5, Azure SQL, Azure Service Bus; nenhum acesso direto do MFE ao serviço.
3. **Contexto e escopo:** API `/api/treinos/**`, chamada pelo Gateway. Produz mensagens consumidas pelo `comunidade-service`.
4. **Estratégia:** REST inbound adapter → application use case → SQL persistence port / event publisher port.
5. **Visão de blocos:** `domain/model` e `domain/event`; `application/port/in`, `application/port/out`, `application/service`; `infrastructure/adapter/in/web` e `out/persistence/sqlserver`, `out/messaging`.
6. **Visão de execução:** GET consulta treinos; POST cria um treino; PUT substitui a lista em transação; após persistência emite `TreinoCriado` ou `PlanoTreinoAtualizado` para a fila.
7. **Visão de implantação:** App Service Java 21. `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVICEBUS_ENABLED`, `SERVICEBUS_SEND_CONNECTION_STRING` e `SERVICEBUS_QUEUE_NAME`.
8. **Conceitos transversais:** validação das categorias/exercícios, transação na substituição da lista, eventos com `eventId` para idempotência no consumidor.
9. **Decisões de arquitetura:** `NVARCHAR(MAX)` guarda as categorias em JSON para preservar o formato consumido pelo MFE; SQL Server usa identidade para IDs.
10. **Requisitos de qualidade:** CRUD, isolamento de persistência, integridade transacional e publicação assíncrona.
11. **Riscos:** a publicação atual é síncrona após a operação SQL; para tolerância a falhas de produção recomenda-se transactional outbox. A fila deve existir no Azure antes de habilitar o envio.
12. **Glossário:** Azure SQL = banco relacional gerenciado; Service Bus = broker de mensagens; outbox = padrão de persistência confiável de eventos.

## Endpoints
- `GET /api/treinos/usuario/{usuarioId}`
- `POST /api/treinos/usuario/{usuarioId}`
- `PUT /api/treinos/usuario/{usuarioId}`
- Swagger: `/swagger-ui.html`

## Como executar localmente
1. Inicie SQL Server local (ex.: `docker compose -f ../docker-compose.local.yml up -d sqlserver`) e crie o banco `FitLife_Treinos`.
2. Defina `DB_URL=jdbc:sqlserver://localhost:1433;databaseName=FitLife_Treinos;encrypt=true;trustServerCertificate=true`, `DB_USERNAME=sa` e `DB_PASSWORD`.
3. Execute manualmente `src/main/resources/schema.sql` no banco `FitLife_Treinos` usando SSMS ou Azure Data Studio. Depois execute `mvn spring-boot:run` com `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `SERVER_PORT=8092`. O SQL de inicialização automática permanece desativado para evitar que o parser do Spring interprete incorretamente blocos T-SQL.
4. Para Service Bus, crie a fila `fitlife-treino-eventos`, configure a conexão com permissão Send e defina `SERVICEBUS_ENABLED=true`. Sem essa configuração, o serviço apenas registra o evento no log.
5. Testes: `mvn test`.

## Segurança
Os endpoints de negócio exigem JWT emitido pelo `usuario-service`. Configure `JWT_ISSUER_URI` e `JWT_JWK_SET_URI` com os mesmos valores usados pelo Gateway. O token identifica o usuário por `sub`, e o serviço confere que o usuário da rota corresponde ao token.

## Alunos
Preencher com os nomes completos dos integrantes do grupo.
