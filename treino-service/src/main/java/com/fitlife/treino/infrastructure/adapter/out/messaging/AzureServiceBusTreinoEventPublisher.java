package com.fitlife.treino.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fitlife.treino.application.port.out.TreinoEventPublisherPort;
import com.fitlife.treino.domain.event.TreinoAtualizadoEvent;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AzureServiceBusTreinoEventPublisher implements TreinoEventPublisherPort {
    private static final Logger log=LoggerFactory.getLogger(AzureServiceBusTreinoEventPublisher.class);
    private final ObjectMapper mapper; private final ServiceBusSenderClient sender; private final boolean enabled;
    public AzureServiceBusTreinoEventPublisher(ObjectMapper mapper,
            @Value("${fitlife.servicebus.enabled:false}") boolean enabled,
            @Value("${fitlife.servicebus.send-connection-string:}") String connection,
            @Value("${fitlife.servicebus.queue-name:fitlife-treino-eventos}") String queue) {
        this.mapper=mapper; this.enabled=enabled;
        if(enabled && (connection==null || connection.isBlank())) throw new IllegalStateException("SERVICEBUS_SEND_CONNECTION_STRING não foi configurada.");
        this.sender=enabled?new ServiceBusClientBuilder().connectionString(connection).sender().queueName(queue).buildClient():null;
        if(!enabled) log.info("Publicação no Azure Service Bus desabilitada. Defina SERVICEBUS_ENABLED=true para ativar.");
    }
    @Override public void publicar(TreinoAtualizadoEvent evento) {
        if(!enabled) { log.info("Evento de domínio gerado (Service Bus desabilitado): {}",evento); return; }
        try {
            String json=mapper.writeValueAsString(evento);
            sender.sendMessage(new ServiceBusMessage(json).setContentType("application/json").setMessageId(evento.eventId()));
            log.info("Evento {} enviado à fila do Azure Service Bus.",evento.eventId());
        } catch(Exception ex) { throw new IllegalStateException("Não foi possível publicar evento no Azure Service Bus.",ex); }
    }
    @PreDestroy public void close(){if(sender!=null)sender.close();}
}
