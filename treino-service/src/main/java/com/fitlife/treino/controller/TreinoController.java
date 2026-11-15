package com.fitlife.treino.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public TreinoController(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Map<String, Object>> listar(@PathVariable long usuarioId) {
        return jdbc.query("SELECT id,titulo,cor,categorias_json FROM treino WHERE usuario_id=? ORDER BY id", (rs, n) -> {
            Map<String, Object> treino = new LinkedHashMap<>();
            treino.put("id", rs.getLong("id"));
            treino.put("titulo", rs.getString("titulo"));
            treino.put("cor", rs.getString("cor"));
            try {
                treino.put("categorias", mapper.readValue(rs.getString("categorias_json"), new TypeReference<List<Map<String, Object>>>() {}));
            } catch (Exception error) {
                treino.put("categorias", List.of());
            }
            return treino;
        }, usuarioId);
    }

    @PostMapping("/usuario/{usuarioId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> criar(@PathVariable long usuarioId, @RequestBody Map<String, Object> dados) {
        validar(dados);
        long id = gravar(usuarioId, dados);
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id", id);
        resposta.put("titulo", dados.get("titulo"));
        return resposta;
    }

    @PutMapping("/usuario/{usuarioId}")
    @Transactional
    public List<Map<String, Object>> substituir(@PathVariable long usuarioId,
                                                 @RequestBody List<Map<String, Object>> treinos) {
        if (treinos == null || treinos.size() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista de treinos é inválida.");
        }
        treinos.forEach(this::validar);
        jdbc.update("DELETE FROM treino WHERE usuario_id=?", usuarioId);
        for (Map<String, Object> treino : treinos) gravar(usuarioId, treino);
        return listar(usuarioId);
    }

    private void validar(Map<String, Object> dados) {
        if (dados == null || dados.get("titulo") == null || dados.get("titulo").toString().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o título da sessão.");
        }
        if (!(dados.get("categorias") instanceof List<?> categorias) || categorias.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe ao menos uma categoria para a sessão.");
        }
        for (Object item : categorias) {
            if (!(item instanceof Map<?, ?> categoria)
                    || categoria.get("nome") == null || categoria.get("nome").toString().isBlank()
                    || !(categoria.get("exercicios") instanceof List<?> exercicios) || exercicios.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cada categoria precisa ter nome e ao menos um exercício.");
            }
        }
    }

    private long gravar(long usuarioId, Map<String, Object> dados) {
        try {
            String json = mapper.writeValueAsString(dados.get("categorias"));
            jdbc.update("INSERT INTO treino(usuario_id,titulo,cor,categorias_json) VALUES(?,?,?,?)",
                    usuarioId, dados.get("titulo"), dados.getOrDefault("cor", "#ff6300"), json);
            return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        } catch (ResponseStatusException error) {
            throw error;
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível gravar o treino.");
        }
    }
}
