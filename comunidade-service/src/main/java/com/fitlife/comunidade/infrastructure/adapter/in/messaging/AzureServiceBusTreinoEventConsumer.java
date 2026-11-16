package com.fitlife.comunidade.infrastructure.adapter.in.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.comunidade.application.port.in.ComunidadeUseCases;
import com.fitlife.comunidade.domain.model.AtividadeTreino;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AzureServiceBusTreinoEventConsumer {
    private static final Logger log=LoggerFactory.getLogger(AzureServiceBusTreinoEventConsumer.class);
    private final boolean enabled; private final String connection; private final String queue;
    private final ObjectMapper mapper; private final ComunidadeUseCases comunidade;
    private ServiceBusProcessorClient processor;
    public AzureServiceBusTreinoEventConsumer(@Value("${fitlife.servicebus.enabled:false}")boolean enabled,
            @Value("${fitlife.servicebus.listen-connection-string:}")String connection,
            @Value("${fitlife.servicebus.queue-name:fitlife-treino-eventos}")String queue,
            ObjectMapper mapper,ComunidadeUseCases comunidade){
        this.enabled=enabled;this.connection=connection;this.queue=queue;this.mapper=mapper;this.comunidade=comunidade;
    }
    @PostConstruct public void iniciar(){
        if(!enabled){log.info("Consumidor do Azure Service Bus desabilitado.");return;}
        if(connection==null||connection.isBlank())throw new IllegalStateException("SERVICEBUS_LISTEN_CONNECTION_STRING não foi configurada.");
        processor=new ServiceBusClientBuilder().connectionString(connection).processor().queueName(queue)
                .processMessage(context->{
                    try{
                        TreinoAtualizadoMessage msg=mapper.readValue(context.getMessage().getBody().toBytes(),TreinoAtualizadoMessage.class);
                        comunidade.registrarAtividadeTreino(new AtividadeTreino(msg.eventId(),msg.usuarioId(),msg.tipo(),msg.totalTreinos(),msg.ocorridoEm()==null?Instant.now():msg.ocorridoEm()));
                        log.info("Evento {} processado pela comunidade.",msg.eventId());
                    }catch(Exception ex){log.error("Falha ao processar mensagem de treino.",ex);throw new IllegalStateException(ex);}
                })
                .processError(context->log.error("Erro no receiver do Azure Service Bus: {}",context.getException().getMessage()))
                .buildProcessorClient();
        processor.start();
        log.info("Consumidor do Azure Service Bus iniciado na fila {}.",queue);
    }
    @PreDestroy public void encerrar(){if(processor!=null){processor.close();processor=null;}}
}
