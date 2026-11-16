package com.fitlife.comunidade.domain.model;
import java.time.Instant;
public record AtividadeTreino(String eventId,long usuarioId,String tipo,int totalTreinos,Instant ocorridoEm) { }
