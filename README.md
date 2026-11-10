# FitLife — Microsservicos

Repositorio agregador dos microsservicos de dominio do FitLife. O projeto utiliza Java 21, Spring Boot 3.5.6 e Maven multi-modulo.

## Estrutura

- `usuario-service`: gerenciamento de usuarios, com persistencia no Azure SQL Database.
- `treino-service`: microsservico independente preparado para o dominio de treinos, com persistencia no MongoDB Atlas.

Cada microsservico possui seu proprio `pom.xml`, configuracao, testes e pode ser empacotado e implantado separadamente. O `pom.xml` da raiz centraliza a versao do Java, a versao do Spring Boot e as dependencias/plugins compartilhados.

## Arquitetura

Os dois servicos seguem a organizacao hexagonal (Ports and Adapters):

- `domain`: modelos e regras centrais do dominio.
- `application/port/in`: casos de uso oferecidos pelo servico.
- `application/port/out`: contratos que a aplicacao necessita de adaptadores externos.
- `application/service`: implementacao dos casos de uso.
- `adapters/in`: entrada HTTP/REST.
- `adapters/out`: persistencia e integracoes externas.
- `config`: configuracoes e composicao de dependencias.

O BFF e o API Gateway ficam em repositorios separados, conforme a organizacao do trabalho. Os microsservicos de dominio nao devem compartilhar diretamente seus bancos de dados.

## Requisitos locais

- JDK 21
- Maven 3.9 ou superior
- Azure SQL Database para `usuario-service`
- MongoDB Atlas ou MongoDB local para `treino-service`

## Compilar e testar todos os modulos

Na raiz do repositorio:

```bash
mvn clean verify
```

## Executar um microsservico

```bash
mvn -pl usuario-service spring-boot:run
```

```bash
mvn -pl treino-service spring-boot:run
```

Configure as variaveis de ambiente descritas nos arquivos `.env.example` antes de executar cada servico. O Maven nao carrega automaticamente arquivos `.env`; configure-as no terminal ou na configuracao de execucao da IDE.

## Swagger

Com o servico em execucao:

- Usuario Service: `http://localhost:8081/swagger-ui.html`
- Treino Service: `http://localhost:8082/swagger-ui.html`

## Testes

Os testes de arquitetura usam ArchUnit e ficam no pacote `architecture` de cada servico. Os testes unitarios do `usuario-service` ficam em `unit`.

O `treino-service` esta preparado com os pacotes, dependencias e teste de arquitetura. Os casos de uso, CRUD e testes unitarios desse microsservico devem ser adicionados depois que o grupo definir os campos e as regras do dominio de treinos; nao foram inventados nesta reorganizacao.

## Banco de dados do Usuario Service

O script inicial de criacao da tabela esta em:

`usuario-service/src/main/resources/db/schema.sql`

Execute-o no Azure SQL Database antes de utilizar os endpoints. A coluna `senha` armazena o hash BCrypt, nao a senha original.
