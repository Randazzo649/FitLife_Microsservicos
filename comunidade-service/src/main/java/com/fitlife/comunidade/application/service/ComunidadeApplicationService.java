package com.fitlife.comunidade.application.service;

import com.fitlife.comunidade.application.port.in.ComunidadeUseCases;
import com.fitlife.comunidade.application.port.out.ComunidadePersistencePort;
import com.fitlife.comunidade.domain.model.AtividadeTreino;
import com.fitlife.comunidade.domain.model.Evento;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ComunidadeApplicationService implements ComunidadeUseCases {
    private final ComunidadePersistencePort comunidade;
    public ComunidadeApplicationService(ComunidadePersistencePort comunidade){this.comunidade=comunidade;}
    @Override public List<Map<String,Object>> listar(Long usuarioId){return comunidade.listar(usuarioId).stream().map(this::resposta).toList();}
    @Override public List<Map<String,Object>> meus(long usuarioId){return comunidade.meus(usuarioId).stream().map(this::resposta).toList();}
    @Override public Map<String,Object> criar(Map<String,Object> dados){
        validar(dados); long usuarioId=numero(dados.get("usuarioId"));
        Evento evento=new Evento(UUID.randomUUID().toString(),texto(dados,"nome"),texto(dados,"data"),texto(dados,"local"),texto(dados,"descricao"),texto(dados,"imagem"),usuarioId,true);
        return resposta(comunidade.criar(evento,usuarioId));
    }
    @Override public Map<String,Object> atualizar(String id,Map<String,Object> dados){
        validar(dados); long usuarioId=numero(dados.get("usuarioId"));
        Evento atual=comunidade.buscarEvento(id).orElseThrow(()->new EventoNotFoundException("Evento não encontrado."));
        if(atual.criadoPor()==null || atual.criadoPor()!=usuarioId) throw new EventoForbiddenException("Somente quem criou pode editar este evento.");
        Evento novo=new Evento(id,texto(dados,"nome"),texto(dados,"data"),texto(dados,"local"),texto(dados,"descricao"),texto(dados,"imagem"),usuarioId,atual.inscrito());
        return resposta(comunidade.atualizar(novo));
    }
    @Override public Map<String,Object> participar(String id,long usuarioId){
        if(comunidade.buscarEvento(id).isEmpty()) throw new EventoNotFoundException("Evento não encontrado.");
        comunidade.participar(id,usuarioId); return Map.of("participando",true,"eventoId",id);
    }
    @Override public List<Map<String,Object>> listarAtividades(Long usuarioId){
        return comunidade.listarAtividades(usuarioId).stream().map(a->{Map<String,Object> m=new LinkedHashMap<>();m.put("eventId",a.eventId());m.put("usuarioId",a.usuarioId());m.put("tipo",a.tipo());m.put("totalTreinos",a.totalTreinos());m.put("ocorridoEm",a.ocorridoEm());return m;}).toList();
    }
    @Override public void registrarAtividadeTreino(AtividadeTreino atividade){comunidade.salvarAtividade(atividade);}
    private void validar(Map<String,Object> d){for(String k:List.of("nome","data","local"))if(d.get(k)==null||d.get(k).toString().isBlank())throw new IllegalArgumentException("Informe nome, data e local do evento.");if(d.get("usuarioId")==null)throw new IllegalArgumentException("Usuário não identificado.");}
    private long numero(Object o){try{return Long.parseLong(String.valueOf(o));}catch(Exception ex){throw new IllegalArgumentException("Usuário não identificado.");}}
    private String texto(Map<String,Object>d,String k){Object o=d.get(k);return o==null?null:o.toString();}
    private Map<String,Object> resposta(Evento e){Map<String,Object> m=new LinkedHashMap<>();m.put("id",e.id());m.put("nome",e.nome());m.put("data",e.data());m.put("local",e.local());m.put("descricao",e.descricao());m.put("imagem",e.imagem());m.put("criado",e.criadoPor()!=null);m.put("criadoPor",e.criadoPor());m.put("inscrito",e.inscrito());return m;}
}
