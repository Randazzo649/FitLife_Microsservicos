# comunidade-service

Serviço REST Spring Boot com JDBC e MySQL; sem JPA.

Banco padrão: `FitLife_Comunidade`. A imagem do evento é armazenada como data URL no MySQL nesta primeira implementação; para produção, migre os binários para Azure Blob Storage.

Porta local padrão: veja `src/main/resources/application.yml`. O API Gateway é o ponto de entrada da aplicação.
