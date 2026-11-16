package com.fitlife.comunidade.infrastructure.adapter.in.messaging;
import java.time.Instant;
public record TreinoAtualizadoMessage(String eventId,String tipo,long usuarioId,int totalTreinos,Instant ocorridoEm) { }
