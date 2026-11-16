package com.fitlife.personal.application.service;

import com.fitlife.personal.application.port.in.PersonalUseCases;
import com.fitlife.personal.application.port.out.PersonalPersistencePort;
import com.fitlife.personal.domain.model.Personal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersonalApplicationService implements PersonalUseCases {
    private final PersonalPersistencePort personais;
    public PersonalApplicationService(PersonalPersistencePort personais){this.personais=personais;}
    @Override public List<Map<String,Object>> listar(String modalidade,String cidade){
        return personais.listar(modalidade,cidade).stream().map(this::resposta).toList();
    }
    @Override public Map<String,Object> registrarContato(long personalId,long usuarioId){
        if(personais.buscarPorId(personalId).isEmpty()) throw new PersonalNotFoundException("Personal não encontrado.");
        personais.registrarContato(usuarioId,personalId);
        return Map.of("enviado",true,"personalId",personalId);
    }
    private Map<String,Object> resposta(Personal p){
        Map<String,Object> map=new LinkedHashMap<>(); map.put("id",p.id()); map.put("nome",p.nome());
        map.put("titulo",p.titulo()); map.put("modalidades",p.modalidades()); map.put("disponibilidade",p.disponibilidade());
        map.put("cidade",p.cidade()); map.put("estado",p.estado()); return map;
    }
}
