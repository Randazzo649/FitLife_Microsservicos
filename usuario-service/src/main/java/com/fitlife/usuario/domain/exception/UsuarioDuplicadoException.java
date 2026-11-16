package com.fitlife.usuario.domain.exception;

/** Indica conflito de unicidade no cadastro de usuário. */
public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String message, Throwable cause) { super(message, cause); }
}
