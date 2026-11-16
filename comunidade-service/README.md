# comunidade-service — Azure SQL + Azure Service Bus

## Resumo
Serviço responsável por eventos da comunidade, inscrições e registro de atividades recebidas do `treino-service` por mensagem. Banco próprio `FitLife_Comunidade`.

## Arquitetura adotada
Arquitetura Hexagonal. REST e Service Bus são adaptadores de entrada; aplicação depende das portas; SQL Server é adaptador de saída.

## Arc42
1. **Introdução e objetivos:** administrar eventos e participações, além de registrar eventos de negócio recebidos do broker.
2. **Restrições:** Java 21, Spring Boot 3.5, Azure SQL e Azure Service Bus.
3. **Contexto e escopo:** REST `/api/comunidade/**` através do Gateway; fila `fitlife-treino-eventos` recebe eventos do serviço de treinos.
4. **Estratégia:** REST/message inbound adapters → use cases → SQL persistence port.
5. **Visão de blocos:** `domain/model`; `application/port/in`, `port/out`, `service`; `infrastructure/adapter/in/web`, `in/messaging`, `out/persistence/sqlserver`.
6. **Visão de execução:** eventos e participações são manipulados via REST; consumidor valida/parseia mensagens `TreinoCriado` e `PlanoTreinoAtualizado` e registra atividade idempotente pelo `eventId`.
7. **Visão de implantação:** App Service Java 21; `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVICEBUS_ENABLED`, `SERVICEBUS_LISTEN_CONNECTION_STRING` e `SERVICEBUS_QUEUE_NAME`.
8. **Conceitos transversais:** autorização do criador na atualização; participação idempotente; consumidor abandona/reprocessa mensagens que falham.
9. **Decisões de arquitetura:** eventos de treino ficam numa tabela de atividade para comprovar o consumo do Service Bus; IDs de usuário são referências externas, sem FK entre bancos.
10. **Requisitos de qualidade:** CRUD de eventos, isolamento de dados e comunicação assíncrona desacoplada.
11. **Riscos:** Service Bus precisa de fila e SAS Listen; habilitar listener apenas depois da fila existir. Em alta disponibilidade, avaliar dead-letter queue e observabilidade.
12. **Glossário:** consumer = consumidor de mensagens; idempotência = reprocessar sem duplicar efeito; DLQ = dead-letter queue.

## Endpoints
- `GET /api/comunidade/eventos`
- `GET /api/comunidade/eventos/usuario/{usuarioId}`
- `POST /api/comunidade/eventos`
- `PUT /api/comunidade/eventos/{id}`
- `POST /api/comunidade/eventos/{id}/participacoes`
- `GET /api/comunidade/atividades?usuarioId=`
- Swagger: `/swagger-ui.html`

## Como executar localmente
1. Crie o banco `FitLife_Comunidade` no SQL Server local. Execute manualmente `src/main/resources/schema.sql` usando SSMS ou Azure Data Studio. Defina `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `SERVER_PORT=8094`; depois execute `mvn spring-boot:run`. O SQL de inicialização automática permanece desativado para executar os blocos T-SQL manualmente.
3. Para consumir Service Bus, crie a fila `fitlife-treino-eventos`, configure SAS com Listen e defina `SERVICEBUS_ENABLED=true` e `SERVICEBUS_LISTEN_CONNECTION_STRING`.
4. Testes: `mvn test`.

## Segurança
Os endpoints de negócio exigem JWT emitido pelo `usuario-service`. Configure `JWT_ISSUER_URI` e `JWT_JWK_SET_URI` com os mesmos valores usados pelo Gateway. O token identifica o usuário por `sub`, e o serviço confere que o usuário da rota corresponde ao token.

## Alunos
Preencher com os nomes completos dos integrantes do grupo.
