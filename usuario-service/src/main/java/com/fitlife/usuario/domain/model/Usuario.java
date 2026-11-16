package com.fitlife.usuario.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Usuario(
        Long id,
        String nome,
        String dataNasc,
        String cpf,
        String genero,
        String telefone,
        String endereco,
        String email,
        String senhaHash,
        BigDecimal peso,
        BigDecimal altura,
        String sexo,
        String frequencia,
        String atividade,
        String cardiopatia,
        String fumo,
        String alcool,
        List<Object> saude,
        String restricao,
        String objetivo,
        String objetivoMedico,
        String fotoUrl,
        Instant criadoEm,
        Instant atualizadoEm) {

    public Usuario {
        saude = saude == null ? new ArrayList<>() : List.copyOf(saude);
    }

    public Usuario comPerfil(Map<String, Object> dados) {
        return new Usuario(id, nome, dataNasc, cpf,
                texto(dados, "genero", genero),
                texto(dados, "telefone", telefone),
                texto(dados, "endereco", endereco),
                texto(dados, "email", email).trim().toLowerCase(), senhaHash, peso, altura, sexo, frequencia,
                atividade, cardiopatia, fumo, alcool, saude, restricao, objetivo,
                objetivoMedico, fotoUrl, criadoEm, Instant.now());
    }

    public Usuario comInformacoesMedicas(Map<String, Object> dados) {
        return new Usuario(id, nome, dataNasc, cpf, genero, telefone, endereco, email,
                senhaHash, decimal(dados.get("peso"), peso), decimal(dados.get("altura"), altura),
                texto(dados, "sexo", sexo), texto(dados, "frequencia", frequencia),
                texto(dados, "atividade", atividade), texto(dados, "cardiopatia", cardiopatia),
                texto(dados, "fumo", fumo), texto(dados, "alcool", alcool),
                lista(dados.get("saude"), saude), texto(dados, "restricao", restricao), objetivo,
                texto(dados, "objetivoMedico", texto(dados, "objetivo_medico", objetivoMedico)),
                fotoUrl, criadoEm, Instant.now());
    }

    public Usuario comFoto(String url) {
        return new Usuario(id, nome, dataNasc, cpf, genero, telefone, endereco, email,
                senhaHash, peso, altura, sexo, frequencia, atividade, cardiopatia, fumo, alcool,
                saude, restricao, objetivo, objetivoMedico, url, criadoEm, Instant.now());
    }

    public Map<String, Object> paraResposta() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id", id);
        resposta.put("nome", nome);
        resposta.put("data_nasc", dataNasc);
        resposta.put("cpf", cpf);
        resposta.put("genero", genero);
        resposta.put("telefone", telefone);
        resposta.put("endereco", endereco);
        resposta.put("email", email);
        resposta.put("peso", peso);
        resposta.put("altura", altura);
        resposta.put("sexo", sexo);
        resposta.put("frequencia", frequencia);
        resposta.put("atividade", atividade);
        resposta.put("cardiopatia", cardiopatia);
        resposta.put("fumo", fumo);
        resposta.put("alcool", alcool);
        resposta.put("saude", saude);
        resposta.put("restricao", restricao);
        resposta.put("objetivo", objetivo);
        resposta.put("objetivoMedico", objetivoMedico);
        resposta.put("fotoUrl", fotoUrl);
        return resposta;
    }

    private static String texto(Map<String, Object> dados, String chave, String atual) {
        Object valor = dados.get(chave);
        return valor == null ? atual : valor.toString();
    }

    private static BigDecimal decimal(Object valor, BigDecimal atual) {
        if (valor == null || valor.toString().isBlank()) return atual;
        try {
            return new BigDecimal(valor.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor numérico inválido: " + valor, ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> lista(Object valor, List<Object> atual) {
        if (valor == null) return atual;
        if (!(valor instanceof List<?> lista)) return atual;
        return (List<Object>) lista;
    }
}
