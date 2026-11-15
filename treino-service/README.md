# treino-service

Serviço REST Spring Boot com JDBC e MySQL; sem JPA.

Banco padrão: `FitLife_Treinos`. O endpoint PUT substitui a lista de sessões do usuário em uma transação, evitando apagar os dados caso a gravação falhe.

Porta local padrão: veja `src/main/resources/application.yml`. O API Gateway é o ponto de entrada da aplicação.
