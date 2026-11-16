# personal-service — Azure SQL

## Resumo
Serviço responsável pela consulta de personal trainers e pelo registro de interesse/contato do usuário. Banco próprio `FitLife_Personais`, separado dos demais serviços.

## Arquitetura adotada
Arquitetura Hexagonal. Controller REST chama a porta de entrada; aplicação aplica regras; adaptador SQL Server implementa a persistência.

## Arc42
1. **Introdução e objetivos:** disponibilizar a busca de personais por modalidade/cidade e registrar contato.
2. **Restrições:** Java 21, Spring Boot 3.5, Azure SQL, acesso pelo API Gateway.
3. **Contexto e escopo:** endpoint `/api/personais/**`; não consulta diretamente o banco de usuários.
4. **Estratégia:** Controller → `PersonalUseCases` → `PersonalPersistencePort` → adaptador JDBC SQL Server.
5. **Visão de blocos:** `domain/model`; `application/port/in`, `port/out`, `service`; `infrastructure/adapter/in/web` e `out/persistence/sqlserver`.
6. **Visão de execução:** pesquisa filtra por modalidade/cidade; contato valida personal existente e faz upsert do interesse.
7. **Visão de implantação:** App Service Java 21; `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
8. **Conceitos transversais:** filtros parametrizados; chave única usuário/personal; seeds demonstrativos idempotentes.
9. **Decisões de arquitetura:** banco próprio `FitLife_Personais`, no mesmo logical server SQL se desejado, mas database separado.
10. **Requisitos de qualidade:** consulta parametrizada, separação de responsabilidades e teste unitário.
11. **Riscos:** dados iniciais são demonstrativos; substituir por cadastro real. Não há vínculo FK com usuários por se tratar de serviço independente.
12. **Glossário:** JDBC = API Java de acesso a dados; Database per Service = banco isolado por microsserviço.

## Endpoints
- `GET /api/personais?modalidade=&cidade=`
- `POST /api/personais/{id}/contatos`
- Swagger: `/swagger-ui.html`

## Como executar localmente
1. Crie o banco `FitLife_Personais` no SQL Server local.
2. Execute manualmente `src/main/resources/schema.sql` conectado ao banco `FitLife_Personais`, usando SSMS ou Azure Data Studio.
3. Defina `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `SERVER_PORT=8093`.
4. Execute `mvn spring-boot:run`.
4. Testes: `mvn test`.

## Segurança
Os endpoints de negócio exigem JWT emitido pelo `usuario-service`. Configure `JWT_ISSUER_URI` e `JWT_JWK_SET_URI` com os mesmos valores usados pelo Gateway. O token identifica o usuário por `sub`, e o serviço confere que o usuário da rota corresponde ao token.

## Alunos
Preencher com os nomes completos dos integrantes do grupo.
