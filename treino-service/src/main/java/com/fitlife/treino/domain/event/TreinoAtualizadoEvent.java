package com.fitlife.treino.domain.event;
import java.time.Instant;
public record TreinoAtualizadoEvent(String eventId, String tipo, long usuarioId, int totalTreinos, Instant ocorridoEm) { }
