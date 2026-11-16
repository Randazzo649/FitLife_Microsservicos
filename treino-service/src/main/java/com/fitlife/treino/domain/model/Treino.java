package com.fitlife.treino.domain.model;
import java.util.List;
import java.util.Map;
public record Treino(Long id, long usuarioId, String titulo, String cor, List<Map<String,Object>> categorias) {
    public Treino { categorias = categorias == null ? List.of() : List.copyOf(categorias); }
}
