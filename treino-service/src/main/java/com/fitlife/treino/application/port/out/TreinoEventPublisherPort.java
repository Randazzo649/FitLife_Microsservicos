package com.fitlife.treino.application.port.out;
import com.fitlife.treino.domain.event.TreinoAtualizadoEvent;
public interface TreinoEventPublisherPort { void publicar(TreinoAtualizadoEvent evento); }
