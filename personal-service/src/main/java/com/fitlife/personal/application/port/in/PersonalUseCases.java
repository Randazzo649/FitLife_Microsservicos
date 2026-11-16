package com.fitlife.personal.application.port.in;
import java.util.List;
import java.util.Map;
public interface PersonalUseCases {
    List<Map<String,Object>> listar(String modalidade, String cidade);
    Map<String,Object> registrarContato(long personalId, long usuarioId);
}
