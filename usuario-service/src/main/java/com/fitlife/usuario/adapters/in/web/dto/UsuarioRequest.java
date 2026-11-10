package com.fitlife.usuario.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Use o formato yyyy-MM-dd") String data_nasc,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 digitos") String cpf,
        @Size(max = 30) String genero,
        @Size(max = 20) String telefone,
        @Size(max = 255) String endereco,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 100) String senha
) {}
