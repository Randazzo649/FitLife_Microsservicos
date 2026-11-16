package com.fitlife.personal.domain.model;
public record Personal(long id, String nome, String titulo, String modalidades,
                       String disponibilidade, String cidade, String estado,
                       String email, String whatsapp) { }
