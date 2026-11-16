package com.fitlife.comunidade.application.port.in;
import com.fitlife.comunidade.domain.model.AtividadeTreino;
import java.util.List;
import java.util.Map;
public interface ComunidadeUseCases {
    List<Map<String,Object>> listar(Long usuarioId);
    List<Map<String,Object>> meus(long usuarioId);
    Map<String,Object> criar(Map<String,Object> dados);
    Map<String,Object> atualizar(String id,Map<String,Object> dados);
    Map<String,Object> participar(String id,long usuarioId);
    List<Map<String,Object>> listarAtividades(Long usuarioId);
    void registrarAtividadeTreino(AtividadeTreino atividade);
}
