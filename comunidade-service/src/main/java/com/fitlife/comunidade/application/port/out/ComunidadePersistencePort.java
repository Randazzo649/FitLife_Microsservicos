package com.fitlife.comunidade.application.port.out;
import com.fitlife.comunidade.domain.model.AtividadeTreino;
import com.fitlife.comunidade.domain.model.Evento;
import java.util.List;
import java.util.Optional;
public interface ComunidadePersistencePort {
    List<Evento> listar(Long usuarioId);
    List<Evento> meus(long usuarioId);
    Optional<Evento> buscarEvento(String id);
    Evento criar(Evento evento,long usuarioId);
    Evento atualizar(Evento evento);
    void participar(String eventoId,long usuarioId);
    void salvarAtividade(AtividadeTreino atividade);
    List<AtividadeTreino> listarAtividades(Long usuarioId);
}
