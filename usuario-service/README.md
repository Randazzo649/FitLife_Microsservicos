# usuario-service

Serviço REST Spring Boot com JDBC e MySQL; sem JPA.

Banco padrão: `FitLife_Usuarios`. Em produção, mantenha o serviço em rede privada, persista a chave RSA de assinatura em Azure Key Vault e use Blob Storage para fotos.

Porta local padrão: veja `src/main/resources/application.yml`. O API Gateway é o ponto de entrada da aplicação.
