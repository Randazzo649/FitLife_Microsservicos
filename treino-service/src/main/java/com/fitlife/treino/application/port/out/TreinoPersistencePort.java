package com.fitlife.treino.application.port.out;
import com.fitlife.treino.domain.model.Treino;
import java.util.List;
public interface TreinoPersistencePort {
    List<Treino> listarPorUsuario(long usuarioId);
    java.util.Optional<Treino> buscar(long usuarioId, long treinoId);
    boolean excluir(long usuarioId, long treinoId);
    Treino inserir(Treino treino);
    List<Treino> substituirTodos(long usuarioId, List<Treino> treinos);
}
