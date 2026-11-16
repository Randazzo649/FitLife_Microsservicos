# usuario-service — MongoDB

## Resumo
Serviço responsável por cadastro, autenticação, consulta/atualização de perfil, informações médicas e foto do usuário. Banco próprio MongoDB (`FitLife_Usuarios`).

## Arquitetura adotada
Arquitetura Hexagonal (Ports and Adapters), organizada em `domain`, `application` e `infrastructure`. O domínio não conhece Spring, MongoDB ou HTTP. Controllers dependem de portas de entrada; o adaptador Mongo implementa a porta de persistência.

## Arc42
1. **Introdução e objetivos:** fornecer identidade e dados de perfil do FitLife.
2. **Restrições:** Java 21, Spring Boot 3.5, MongoDB/Cosmos DB com API Mongo compatível; comunicação externa apenas pelo Gateway.
3. **Contexto e escopo:** recebe rotas `/api/usuarios/**` e `/oauth2/jwks` encaminhadas pelo API Gateway; persiste somente dados de usuário.
4. **Estratégia:** portas de entrada REST, casos de uso na aplicação, portas de saída e adaptadores MongoDB, BCrypt, JWT e armazenamento de fotos.
5. **Visão de blocos:** `domain/model`; `application/port/in`, `application/port/out`, `application/service`; `infrastructure/adapter/in/web`, `infrastructure/adapter/out/persistence/mongo`, `security` e `storage`.
6. **Visão de execução:** cadastro valida dados, gera hash BCrypt, cria ID sequencial no Mongo e persiste documento. Login valida hash e emite JWT RS256. JWKS publica a chave pública.
7. **Visão de implantação:** App Service Java 21 separado; `MONGODB_URI`, `MONGODB_DATABASE`, `JWT_ISSUER_URI` e `FITLIFE_UPLOAD_DIR` por configuração de ambiente.
8. **Conceitos transversais:** validação, BCrypt, JWT/JWKS, índices únicos para CPF/e-mail, limite de foto 2 MB.
9. **Decisões de arquitetura:** IDs numéricos são mantidos para compatibilidade dos contratos dos demais serviços; Mongo usa coleção de contadores atômicos. A chave JWT gerada em memória é adequada apenas para desenvolvimento; produção deve usar chave persistente/Key Vault.
10. **Requisitos de qualidade:** isolamento de persistência, contratos REST estáveis, tratamento de conflitos e testes unitários/ArchUnit.
11. **Riscos:** armazenamento local de fotos não é durável em App Service; migrar para Blob Storage. Persistir/compartilhar chave JWT antes de escalar ou reiniciar em produção.
12. **Glossário:** BFF = Backend for Frontend; JWKS = JSON Web Key Set; BCrypt = algoritmo de hash de senhas.

## Endpoints
- `POST /api/usuarios/cadastro`
- `POST /api/usuarios/login`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `PUT /api/usuarios/{id}/informacoes-medicas`
- `PUT /api/usuarios/{id}/foto`
- `GET /oauth2/jwks`
- Swagger: `/swagger-ui.html`

## Como executar localmente
1. Inicie MongoDB com `docker compose -f ../docker-compose.local.yml up -d mongodb` (ou use uma instância MongoDB compatível).
2. Execute `mvn spring-boot:run` nesta pasta, com `MONGODB_URI=mongodb://localhost:27017/FitLife_Usuarios`, `JWT_ISSUER_URI=http://localhost:8091` e `SERVER_PORT=8091`. Para desenvolvimento local, o serviço gera uma chave RSA temporária quando as variáveis de chave não são informadas.
3. Testes: `mvn test`.

## Segurança
Os endpoints de negócio exigem JWT emitido pelo `usuario-service`. Configure `JWT_ISSUER_URI` e `JWT_JWK_SET_URI` com os mesmos valores usados pelo Gateway. O token identifica o usuário por `sub`, e o serviço confere que o usuário da rota corresponde ao token.

## Alunos
Preencher com os nomes completos dos integrantes do grupo.
