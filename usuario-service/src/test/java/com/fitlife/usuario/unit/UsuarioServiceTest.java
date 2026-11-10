package com.fitlife.usuario.unit;

import com.fitlife.usuario.application.port.out.SenhaEncoderPort;
import com.fitlife.usuario.application.port.out.UsuarioRepositoryPort;
import com.fitlife.usuario.application.service.UsuarioService;
import com.fitlife.usuario.domain.exception.EmailJaCadastradoException;
import com.fitlife.usuario.domain.exception.UsuarioNaoEncontradoException;
import com.fitlife.usuario.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepositoryPort repository;
    @Mock
    private SenhaEncoderPort senhaEncoder;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(repository, senhaEncoder);
    }

    @Test
    void deveCadastrarUsuarioComSenhaCodificada() {
        Usuario usuario = usuarioValido();
        when(repository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(senhaEncoder.codificar("senha123" )).thenReturn("hash-da-senha");
        when(repository.inserir(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.cadastrar(usuario);

        assertEquals("hash-da-senha", resultado.getSenha());
        verify(repository).inserir(usuario);
    }

    @Test
    void naoDeveCadastrarEmailJaExistente() {
        Usuario usuario = usuarioValido();
        when(repository.buscarPorEmail(usuario.getEmail())).thenReturn(Optional.of(usuarioValido()));

        assertThrows(EmailJaCadastradoException.class, () -> service.cadastrar(usuario));
        verify(repository, never()).inserir(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    private Usuario usuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setNome("Pessoa Teste");
        usuario.setData_nasc("2000-01-01");
        usuario.setCpf("12345678900");
        usuario.setEmail("teste@fitlife.com");
        usuario.setSenha("senha123");
        return usuario;
    }
}
