package com.fitlife.treino.application.port.in;
import java.util.List;
import java.util.Map;
public interface TreinoUseCases {
    List<Map<String,Object>> listar(long usuarioId);
    Map<String,Object> buscar(long usuarioId, long treinoId);
    void excluir(long usuarioId, long treinoId);
    Map<String,Object> criar(long usuarioId, Map<String,Object> dados);
    List<Map<String,Object>> substituir(long usuarioId, List<Map<String,Object>> treinos);
}
