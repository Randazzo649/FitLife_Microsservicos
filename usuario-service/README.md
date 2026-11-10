# Usuario Service

Microsservico de dominio responsavel pelo cadastro, consulta, atualizacao e exclusao de usuarios do FitLife. Utiliza Azure SQL Database e JDBC.

## Executar

Configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` conforme o arquivo `.env.example`. O Maven nao carrega esse arquivo automaticamente: configure as variaveis no terminal ou na IDE. Execute `schema.sql` no banco e rode:

```bash
mvn spring-boot:run
```

A API fica disponivel na porta 8081, por padrao. Documentacao Swagger: `http://localhost:8081/swagger-ui.html`.

## Endpoints

- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `GET /api/usuarios/buscar?email=...`
- `POST /api/usuarios`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

A senha recebida e codificada com BCrypt e nunca e incluida no DTO de resposta.
