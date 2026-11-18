# FitLife — Backend (Microsserviços)

Repositório público do backend do FitLife. O API Gateway e o monorepo de Microfrontends permanecem em repositórios separados, totalizando os três projetos solicitados: **Frontend**, **API Gateway** e **Backend**.

## Arquitetura adotada

Os quatro microsserviços adotam **Arquitetura Hexagonal (Ports and Adapters)**. Cada aplicação separa `domain`, `application` e `infrastructure`. Controllers REST e consumidor do Service Bus são adaptadores de entrada; persistência, JWT, armazenamento de fotos e publicação no Service Bus são adaptadores de saída. O domínio não conhece Spring, bancos, HTTP ou SDK do Azure.

## Estrutura do repositório

```text
FitLife_Microsservicos/
├── pom.xml                         # agregador Maven dos quatro serviços
├── docker-compose.local.yml        # MongoDB + SQL Server para desenvolvimento local
├── .env.example
├── usuario-service/                # MongoDB / Cosmos DB API for MongoDB
├── treino-service/                 # Azure SQL + publica eventos
├── personal-service/               # Azure SQL independente
└── comunidade-service/             # Azure SQL independente + consome eventos
```

O API Gateway não está dentro deste repositório. Ele possui seu próprio `pom.xml`, README e workflow no repositório `FitLife_API_Gateway`.

## Database per Service

| Microsserviço | Persistência | Database/coleção |
|---|---|---|
| `usuario-service` | MongoDB ou Cosmos DB API for MongoDB | `FitLife_Usuarios` / `usuarios` |
| `treino-service` | Azure SQL Database | `FitLife_Treinos` |
| `personal-service` | Azure SQL Database | `FitLife_Personais` |
| `comunidade-service` | Azure SQL Database | `FitLife_Comunidade` |

Os três bancos SQL são databases independentes. Podem compartilhar o mesmo logical server Azure SQL, mas cada microsserviço usa sua própria database e credenciais no ambiente Azure. Não existem consultas ou chaves estrangeiras entre bancos de microsserviços. IDs de usuário são referências externas nos serviços de treino, personal e comunidade.

## Responsabilidades e endpoints

### `usuario-service` — MongoDB
- `POST /api/usuarios/cadastro` — cadastro público.
- `POST /api/usuarios/login` — autenticação pública e emissão de JWT RS256.
- `GET /api/usuarios/{id}` — consulta do próprio usuário.
- `PUT /api/usuarios/{id}` — atualização de perfil.
- `PUT /api/usuarios/{id}/informacoes-medicas` — atualização de dados médicos.
- `PUT /api/usuarios/{id}/foto` — atualização de foto (multipart).
- `DELETE /api/usuarios/{id}` — exclusão da própria conta.
- `GET /oauth2/jwks` — chave pública do emissor JWT.

### `treino-service` — Azure SQL
- `GET /api/treinos/usuario/{usuarioId}` — lista os treinos.
- `GET /api/treinos/usuario/{usuarioId}/{treinoId}` — consulta um treino.
- `POST /api/treinos/usuario/{usuarioId}` — cria um treino.
- `PUT /api/treinos/usuario/{usuarioId}` — substitui o plano do usuário.
- `DELETE /api/treinos/usuario/{usuarioId}/{treinoId}` — exclui um treino.

### `personal-service` — Azure SQL
- `GET /api/personais?modalidade=&cidade=` — busca por modalidade e cidade.
- `POST /api/personais/{id}/contatos` — registra interesse/contato do usuário.

### `comunidade-service` — Azure SQL
- `GET /api/comunidade/eventos?usuarioId=` — lista eventos e status de participação.
- `GET /api/comunidade/eventos/usuario/{usuarioId}` — eventos do usuário.
- `POST /api/comunidade/eventos` — cria evento.
- `PUT /api/comunidade/eventos/{id}` — atualiza evento criado pelo usuário.
- `POST /api/comunidade/eventos/{id}/participacoes` — participa de evento.
- `GET /api/comunidade/atividades` — consulta atividades recebidas por mensageria.

Todos os microsserviços disponibilizam OpenAPI em `/v3/api-docs` e Swagger UI em `/swagger-ui.html`. As operações protegidas exigem JWT. Cadastro, login e JWKS são públicos no `usuario-service`.

## Comunicação assíncrona — Azure Service Bus

A regra de negócio demonstrada é: quando o `treino-service` cria um treino ou substitui o plano do usuário, publica um evento na fila `fitlife-treino-eventos`. O `comunidade-service` consome o evento e registra a atividade de treino na sua própria database SQL.

Eventos enviados: `TreinoCriado` e `PlanoTreinoAtualizado`. O campo `eventId` é utilizado como chave idempotente em `atividade_treino`, evitando efeitos duplicados no banco da comunidade.

O Service Bus fica desabilitado por padrão em desenvolvimento. Para a demonstração na Azure, crie uma fila `fitlife-treino-eventos`, crie uma política SAS com permissão **Send** para o serviço de treinos e outra com permissão **Listen** para o serviço de comunidade. Configure `SERVICEBUS_ENABLED=true` e as connection strings apenas em App Settings/Key Vault; não as versione no Git.

O publisher envia a mensagem após a persistência SQL. Para resiliência de produção contra falha entre commit e publicação, a evolução recomendada é Transactional Outbox; esta entrega não declara implementar Outbox.

## Executar localmente

### Pré-requisitos
- Java 21.
- Maven 3.9 ou superior.
- Docker Desktop com Docker Compose.
- SQL Server Management Studio ou Azure Data Studio para executar T-SQL.

### 1. Iniciar os bancos locais

Copie `.env.example` para `.env`, altere `DB_PASSWORD` e inicie os containers:

```bash
docker compose -f docker-compose.local.yml up -d
```

O Compose inicia MongoDB em `localhost:27017` e SQL Server em `localhost:1433`. O arquivo `.env` é lido pelo Docker Compose; as variáveis da aplicação Spring Boot devem ser definidas no terminal de cada microsserviço.

### 2. Criar as databases SQL

Conecte ao SQL Server local e execute:

```sql
CREATE DATABASE FitLife_Treinos;
CREATE DATABASE FitLife_Personais;
CREATE DATABASE FitLife_Comunidade;
```

### 3. Criar as tabelas

Execute manualmente, conectado à database correspondente, os scripts:

- `treino-service/src/main/resources/schema.sql` em `FitLife_Treinos`;
- `personal-service/src/main/resources/schema.sql` em `FitLife_Personais`;
- `comunidade-service/src/main/resources/schema.sql` em `FitLife_Comunidade`.

A inicialização automática SQL permanece desativada (`spring.sql.init.mode=never`) porque os scripts usam blocos T-SQL `IF/BEGIN/END`; executá-los manualmente evita que um parser SQL genérico divida esses blocos incorretamente.

### 4. Iniciar cada microsserviço

Abra um terminal para cada aplicação. Exemplos em PowerShell:

**Usuários (MongoDB):**

```powershell
cd usuario-service
$env:SERVER_PORT="8091"
$env:MONGODB_URI="mongodb://localhost:27017/FitLife_Usuarios"
$env:MONGODB_DATABASE="FitLife_Usuarios"
$env:JWT_ISSUER_URI="http://localhost:8091"
mvn spring-boot:run
```

**Treinos (Azure SQL/SQL Server local):**

```powershell
cd treino-service
$env:SERVER_PORT="8092"
$env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=FitLife_Treinos;encrypt=true;trustServerCertificate=true"
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="SUA_SENHA_LOCAL"
$env:JWT_ISSUER_URI="http://localhost:8091"
$env:JWT_JWK_SET_URI="http://localhost:8091/oauth2/jwks"
mvn spring-boot:run
```

**Personais:** use a mesma configuração SQL, com `databaseName=FitLife_Personais` e `SERVER_PORT=8093`.

**Comunidade:** use a mesma configuração SQL, com `databaseName=FitLife_Comunidade` e `SERVER_PORT=8094`. Para testar o Service Bus localmente, mantenha `SERVICEBUS_ENABLED=false`; o consumo real será demonstrado após criar a fila no Azure.

O Gateway separado deve apontar para `http://localhost:8091`, `8092`, `8093` e `8094`, e usar `JWT_ISSUER_URI=http://localhost:8091` e `JWT_JWK_SET_URI=http://localhost:8091/oauth2/jwks`.

### 5. Executar os testes

Na raiz deste repositório:

```bash
mvn -B test
```

O POM raiz agrega os quatro microsserviços. Cada serviço também pode ser testado individualmente com `mvn test` dentro da respectiva pasta.

## Configuração na Azure

Cada microsserviço precisa ser executado como uma aplicação independente. As quatro aplicações não devem ser empacotadas em um único processo Spring Boot: isso eliminaria o isolamento de implantação esperado de microservices. Use uma unidade de execução por serviço (por exemplo, App Service separado ou Azure Container Apps). O plano de hospedagem pode ser compartilhado quando suportar os recursos necessários; memória e CPU são compartilhadas entre aplicações do mesmo App Service Plan.

Configure `SERVER_PORT=8080` em cada App Service. Use as seguintes App Settings:

| Serviço | App Settings principais |
|---|---|
| `usuario-service` | `MONGODB_URI`, `MONGODB_DATABASE`, `JWT_ISSUER_URI`, `JWT_PRIVATE_KEY_BASE64`, `JWT_PUBLIC_KEY_BASE64`, `SERVER_PORT` |
| `treino-service` | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_ISSUER_URI`, `JWT_JWK_SET_URI`, `SERVICEBUS_ENABLED`, `SERVICEBUS_SEND_CONNECTION_STRING`, `SERVICEBUS_QUEUE_NAME`, `SERVER_PORT` |
| `personal-service` | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_ISSUER_URI`, `JWT_JWK_SET_URI`, `SERVER_PORT` |
| `comunidade-service` | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_ISSUER_URI`, `JWT_JWK_SET_URI`, `SERVICEBUS_ENABLED`, `SERVICEBUS_LISTEN_CONNECTION_STRING`, `SERVICEBUS_QUEUE_NAME`, `SERVER_PORT` |

Para Azure SQL, use uma JDBC URL no formato `jdbc:sqlserver://<servidor>.database.windows.net:1433;databaseName=<database>;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;`. Configure firewall/regras de rede do SQL Server para permitir conexões dos ambientes de execução. Use logins separados por database sempre que possível.

O `JWT_ISSUER_URI` precisa ser exatamente o mesmo nos serviços e no Gateway: a URL HTTPS pública do `usuario-service`. `JWT_JWK_SET_URI` deve apontar para `<URL_DO_USUARIO_SERVICE>/oauth2/jwks`. Gere um par RSA estável para Azure e configure as duas chaves em App Settings ou Key Vault. Não use a geração efêmera local em produção, porque reiniciar o serviço invalidaria os tokens emitidos com a chave anterior.

A foto de perfil utiliza adaptador de armazenamento local nesta versão. O disco local do App Service não deve ser tratado como armazenamento durável/compartilhado; para uso real, migre esse adaptador para Azure Blob Storage.

## Testes e qualidade

- JUnit 5 e Mockito: testes unitários dos casos de uso, com dependências de saída simuladas por portas.
- ArchUnit: regras de independência entre camadas, proibição de dependência de adaptadores externos pelo domínio, proibição de Spring/infraestrutura na aplicação, proibição de acesso de adaptadores de entrada aos adaptadores de saída e ausência de ciclos.
- GitHub Actions: workflow de CI executa `mvn -B test` nos quatro projetos em push/PR.

Não considere a suíte aprovada até o GitHub Actions ou um ambiente local mostrar `BUILD SUCCESS`. A preparação deste pacote foi revisada estaticamente; não foi possível executar Maven neste ambiente, pois Maven não está instalado e o acesso ao Maven Central não estava disponível.

## Documentação Arc42

Cada microsserviço possui README próprio com as 12 seções Arc42: introdução e objetivos, restrições, contexto e escopo, estratégia, visão de blocos, visão de execução, implantação, conceitos transversais, decisões, requisitos de qualidade, riscos e glossário.

## Alunos

- Amanda Milleo
- Camila Araújo
- Pedro Henrique Kleim Vieira
- Riccardo Randazzo
- Yuri Sobezak

## Vídeo
https://www.youtube.com/watch?v=Sv49b03aIbU&feature=youtu.be
