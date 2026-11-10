package com.fitlife.usuario.domain.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
