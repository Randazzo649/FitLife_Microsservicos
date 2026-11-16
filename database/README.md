# Bancos de dados do FitLife — Parte 1

Esta pasta documenta a distribuição dos bancos por microsserviço. Cada serviço é dono dos próprios dados; nenhum serviço deve consultar diretamente o banco de outro serviço.

## Distribuição

| Microsserviço | Tecnologia | Database | Estruturas principais |
|---|---|---|---|
| `usuario-service` | MongoDB ou Azure Cosmos DB for MongoDB | `FitLife_Usuarios` | coleção `usuarios` e coleção técnica `counters` |
| `treino-service` | Azure SQL Database | `FitLife_Treinos` | tabela `dbo.treino` |
| `personal-service` | Azure SQL Database | `FitLife_Personais` | tabelas `dbo.personal`, `dbo.contato_personal` |
| `comunidade-service` | Azure SQL Database | `FitLife_Comunidade` | tabelas `dbo.evento`, `dbo.evento_participante`, `dbo.atividade_treino` |

Os bancos SQL podem ficar no mesmo logical server, mas devem continuar sendo databases independentes. Não crie as três estruturas SQL dentro de uma única database.

## Desenvolvimento local

1. Na raiz do repositório, copie `.env.example` para `.env` e altere `DB_PASSWORD` para uma senha local forte.
2. Inicie os bancos:

   ```bash
   docker compose -f docker-compose.local.yml up -d
   ```

3. Conecte ao SQL Server local usando SSMS ou Azure Data Studio e execute `sqlserver/00-create-local-databases.sql` conectado ao banco `master`.
4. Abra cada database e execute o `schema.sql` correspondente:
   - `treino-service/src/main/resources/schema.sql` em `FitLife_Treinos`;
   - `personal-service/src/main/resources/schema.sql` em `FitLife_Personais`;
   - `comunidade-service/src/main/resources/schema.sql` em `FitLife_Comunidade`.
5. O `usuario-service` cria/usa o database e as coleções MongoDB por meio da URI configurada. Os índices únicos de CPF/e-mail são criados pelo Spring Data MongoDB, pois `auto-index-creation` está habilitado.

## Azure

Os arquivos deste repositório configuram as aplicações para se conectarem aos bancos; eles **não criam os recursos Azure automaticamente**. Crie o MongoDB compatível (MongoDB Atlas ou Azure Cosmos DB for MongoDB) e as três Azure SQL Databases na assinatura do grupo.

### MongoDB / Cosmos DB for MongoDB

Configure no App Service do `usuario-service`:

- `MONGODB_URI`: connection string completa, com o database `FitLife_Usuarios` e opções TLS/retry fornecidas pelo provedor;
- `MONGODB_DATABASE`: `FitLife_Usuarios`.

Não versione a connection string. Cadastre-a em App Settings ou Key Vault.

### Azure SQL Database

Crie databases independentes `FitLife_Treinos`, `FitLife_Personais` e `FitLife_Comunidade`. Em cada uma, execute apenas o `schema.sql` do serviço correspondente.

Configure em cada App Service:

- `DB_URL`: `jdbc:sqlserver://<servidor>.database.windows.net:1433;databaseName=<database>;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;`;
- `DB_USERNAME`: usuário SQL autorizado naquela database;
- `DB_PASSWORD`: senha correspondente.

Use uma database e credenciais próprias para cada serviço. Configure as regras de rede/firewall do logical server para permitir a origem dos serviços. Não coloque credenciais no GitHub nem nos arquivos versionados.

## Limite desta entrega

Esta parte entrega o código dos quatro microsserviços, adaptadores de persistência, scripts de estrutura SQL, configuração local MongoDB/SQL Server, testes e instruções. A criação dos recursos gerenciados na Azure, o preenchimento de secrets e a validação de conexão serão feitos na etapa de implantação.
