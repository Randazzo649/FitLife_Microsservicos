package com.fitlife.usuario.application.service;

import com.fitlife.usuario.application.exception.ApplicationException;
import com.fitlife.usuario.application.port.in.UsuarioUseCases;
import com.fitlife.usuario.application.port.out.JwtTokenPort;
import com.fitlife.usuario.application.port.out.PasswordHasherPort;
import com.fitlife.usuario.application.port.out.ProfilePhotoStoragePort;
import com.fitlife.usuario.application.port.out.UsuarioPersistencePort;
import com.fitlife.usuario.domain.model.Usuario;
import com.fitlife.usuario.domain.exception.UsuarioDuplicadoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsuarioApplicationService implements UsuarioUseCases {
    private final UsuarioPersistencePort usuarios;
    private final PasswordHasherPort senhas;
    private final JwtTokenPort tokens;
    private final ProfilePhotoStoragePort fotos;

    public UsuarioApplicationService(UsuarioPersistencePort usuarios, PasswordHasherPort senhas,
                                     JwtTokenPort tokens, ProfilePhotoStoragePort fotos) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
        this.fotos = fotos;
    }

    @Override
    public Map<String, Object> cadastrar(Map<String, Object> dados) {
        String nome = texto(dados, "nome");
        String cpf = texto(dados, "cpf");
        String email = texto(dados, "email").toLowerCase();
        String senha = texto(dados, "senha");
        if (nome.isBlank() || cpf.isBlank() || email.isBlank() || senha.length() < 8) {
            throw new ApplicationException(400, "Informe nome, CPF, e-mail e uma senha com pelo menos 8 caracteres.");
        }
        if (usuarios.buscarPorEmail(email).isPresent()) {
            throw new ApplicationException(409, "Já existe uma conta com este e-mail.");
        }
        if (usuarios.existeCpf(cpf)) {
            throw new ApplicationException(409, "Já existe uma conta com este CPF.");
        }

        Instant agora = Instant.now();
        Usuario novo = new Usuario(usuarios.proximoId(), nome,
                primeiro(dados, "data_nasc", "nascimento"), cpf,
                primeiro(dados, "genero", "sexo"), texto(dados, "telefone"), texto(dados, "endereco"),
                email, senhas.gerarHash(senha), decimal(dados.get("peso")), decimal(dados.get("altura")),
                texto(dados, "sexo"), texto(dados, "frequencia"), texto(dados, "atividade"),
                texto(dados, "cardiopatia"), texto(dados, "fumo"), texto(dados, "alcool"),
                lista(dados.get("saude")), texto(dados, "restricao"), texto(dados, "objetivo"),
                primeiro(dados, "objetivoMedico", "objetivo_medico"), null, agora, agora);
        try {
            usuarios.salvar(novo);
        } catch (UsuarioDuplicadoException ex) {
            throw new ApplicationException(409, "CPF ou e-mail já cadastrado.");
        }
        return Map.of("id", novo.id(), "message", "Cadastro realizado com sucesso.");
    }

    @Override
    public Map<String, Object> login(Map<String, Object> credenciais) {
        String email = texto(credenciais, "email").toLowerCase();
        String senha = texto(credenciais, "senha");
        Usuario usuario = usuarios.buscarPorEmail(email)
                .orElseThrow(() -> new ApplicationException(401, "E-mail ou senha inválidos."));
        if (!senhas.corresponde(senha, usuario.senhaHash())) {
            throw new ApplicationException(401, "E-mail ou senha inválidos.");
        }
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("accessToken", tokens.emitir(usuario.id(), usuario.email()));
        resposta.put("tokenType", "Bearer");
        resposta.put("expiresIn", 28800);
        resposta.put("usuario", usuario.paraResposta());
        return resposta;
    }

    @Override
    public Map<String, Object> buscar(long id) { return obter(id).paraResposta(); }

    @Override
    public void excluir(long id) {
        obter(id);
        if (!usuarios.excluir(id)) {
            throw new ApplicationException(404, "Usuário não encontrado.");
        }
    }

    @Override
    public Map<String, Object> atualizarPerfil(long id, Map<String, Object> dados) {
        Usuario atual = obter(id);
        String email = texto(dados, "email");
        if (!email.isBlank() && !email.equalsIgnoreCase(atual.email())
                && usuarios.buscarPorEmail(email.toLowerCase()).isPresent()) {
            throw new ApplicationException(409, "Este e-mail já está sendo utilizado.");
        }
        Usuario atualizado = atual.comPerfil(dados);
        try {
            return usuarios.salvar(atualizado).paraResposta();
        } catch (UsuarioDuplicadoException ex) {
            throw new ApplicationException(409, "CPF ou e-mail já cadastrado.");
        }
    }

    @Override
    public Map<String, Object> atualizarInformacoesMedicas(long id, Map<String, Object> dados) {
        Usuario atualizado = obter(id).comInformacoesMedicas(dados);
        return usuarios.salvar(atualizado).paraResposta();
    }

    @Override
    public Map<String, Object> atualizarFoto(long id, byte[] conteudo, String contentType) throws IOException {
        Usuario usuario = obter(id);
        if (conteudo == null || conteudo.length == 0 || conteudo.length > 2 * 1024 * 1024) {
            throw new ApplicationException(400, "A imagem deve ter até 2 MB.");
        }
        if (!List.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
            throw new ApplicationException(400, "Envie uma imagem JPEG, PNG ou WebP.");
        }
        String url = fotos.salvar(id, conteudo, contentType);
        return Map.of("fotoUrl", usuarios.salvar(usuario.comFoto(url)).fotoUrl());
    }

    private Usuario obter(long id) {
        return usuarios.buscarPorId(id).orElseThrow(() -> new ApplicationException(404, "Usuário não encontrado."));
    }

    private static String texto(Map<String, Object> dados, String chave) {
        Object valor = dados.get(chave);
        return valor == null ? "" : valor.toString().trim();
    }

    private static String primeiro(Map<String, Object> dados, String a, String b) {
        String valor = texto(dados, a);
        return valor.isBlank() ? texto(dados, b) : valor;
    }

    private static BigDecimal decimal(Object valor) {
        if (valor == null || valor.toString().isBlank()) return null;
        try { return new BigDecimal(valor.toString()); }
        catch (NumberFormatException ex) { throw new ApplicationException(400, "Valor numérico inválido: " + valor); }
    }

    private static List<Object> lista(Object valor) {
        return valor instanceof List<?> itens ? List.copyOf(itens) : List.of();
    }
}
