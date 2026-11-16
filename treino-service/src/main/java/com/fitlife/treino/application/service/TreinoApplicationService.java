package com.fitlife.treino.application.service;

import com.fitlife.treino.application.port.in.TreinoUseCases;
import com.fitlife.treino.application.port.out.TreinoEventPublisherPort;
import com.fitlife.treino.application.port.out.TreinoPersistencePort;
import com.fitlife.treino.domain.event.TreinoAtualizadoEvent;
import com.fitlife.treino.domain.model.Treino;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TreinoApplicationService implements TreinoUseCases {
    private final TreinoPersistencePort treinos;
    private final TreinoEventPublisherPort eventos;
    public TreinoApplicationService(TreinoPersistencePort treinos, TreinoEventPublisherPort eventos) {
        this.treinos=treinos; this.eventos=eventos;
    }
    @Override public List<Map<String,Object>> listar(long usuarioId) {
        return treinos.listarPorUsuario(usuarioId).stream().map(this::resposta).toList();
    }
    @Override public Map<String,Object> buscar(long usuarioId, long treinoId) {
        return treinos.buscar(usuarioId, treinoId).map(this::resposta)
                .orElseThrow(() -> new TreinoNotFoundException("Treino não encontrado para este usuário."));
    }
    @Override public void excluir(long usuarioId, long treinoId) {
        if (!treinos.excluir(usuarioId, treinoId)) {
            throw new TreinoNotFoundException("Treino não encontrado para este usuário.");
        }
    }
    @Override public Map<String,Object> criar(long usuarioId, Map<String,Object> dados) {
        validar(dados);
        Treino salvo=treinos.inserir(converter(usuarioId,dados));
        eventos.publicar(new TreinoAtualizadoEvent(UUID.randomUUID().toString(),"TreinoCriado",usuarioId,1,Instant.now()));
        Map<String,Object> resposta=new LinkedHashMap<>(); resposta.put("id",salvo.id()); resposta.put("titulo",salvo.titulo());
        return resposta;
    }
    @Override public List<Map<String,Object>> substituir(long usuarioId, List<Map<String,Object>> dados) {
        if (dados==null || dados.size()>100) throw new IllegalArgumentException("A lista de treinos é inválida.");
        dados.forEach(this::validar);
        List<Treino> novos=dados.stream().map(d->converter(usuarioId,d)).toList();
        List<Map<String,Object>> resposta=treinos.substituirTodos(usuarioId,novos).stream().map(this::resposta).toList();
        eventos.publicar(new TreinoAtualizadoEvent(UUID.randomUUID().toString(),"PlanoTreinoAtualizado",usuarioId,resposta.size(),Instant.now()));
        return resposta;
    }
    private void validar(Map<String,Object> dados) {
        if (dados==null || dados.get("titulo")==null || dados.get("titulo").toString().isBlank()) throw new IllegalArgumentException("Informe o título da sessão.");
        if (!(dados.get("categorias") instanceof List<?> categorias) || categorias.isEmpty()) throw new IllegalArgumentException("Informe ao menos uma categoria para a sessão.");
        for (Object item:categorias) {
            if (!(item instanceof Map<?,?> categoria) || categoria.get("nome")==null || categoria.get("nome").toString().isBlank()
                    || !(categoria.get("exercicios") instanceof List<?> exercicios) || exercicios.isEmpty())
                throw new IllegalArgumentException("Cada categoria precisa ter nome e ao menos um exercício.");
            if (exercicios.stream().anyMatch(e->e==null || e.toString().isBlank())) throw new IllegalArgumentException("Os exercícios não podem estar vazios.");
        }
    }
    @SuppressWarnings("unchecked") private Treino converter(long usuarioId,Map<String,Object> dados) {
        List<Map<String,Object>> categorias=(List<Map<String,Object>>)dados.get("categorias");
        return new Treino(null,usuarioId,dados.get("titulo").toString(),String.valueOf(dados.getOrDefault("cor","#ff6300")),new ArrayList<>(categorias));
    }
    private Map<String,Object> resposta(Treino treino) {
        Map<String,Object> map=new LinkedHashMap<>(); map.put("id",treino.id()); map.put("titulo",treino.titulo());
        map.put("cor",treino.cor()); map.put("categorias",treino.categorias()); return map;
    }
}
