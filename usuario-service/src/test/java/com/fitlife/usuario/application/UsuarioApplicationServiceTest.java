package com.fitlife.usuario.application;

import com.fitlife.usuario.application.exception.ApplicationException;
import com.fitlife.usuario.application.port.out.JwtTokenPort;
import com.fitlife.usuario.application.port.out.PasswordHasherPort;
import com.fitlife.usuario.application.port.out.ProfilePhotoStoragePort;
import com.fitlife.usuario.application.port.out.UsuarioPersistencePort;
import com.fitlife.usuario.application.service.UsuarioApplicationService;
import com.fitlife.usuario.domain.model.Usuario;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {
    @Mock UsuarioPersistencePort usuarios;
    @Mock PasswordHasherPort senhas;
    @Mock JwtTokenPort tokens;
    @Mock ProfilePhotoStoragePort fotos;
    UsuarioApplicationService service;

    @BeforeEach void setUp(){service=new UsuarioApplicationService(usuarios,senhas,tokens,fotos);}

    @Test void deveCadastrarUsuarioComSenhaHash(){
        when(usuarios.buscarPorEmail("ana@example.com")).thenReturn(Optional.empty());
        when(usuarios.existeCpf("12345678900")).thenReturn(false);
        when(usuarios.proximoId()).thenReturn(1L);
        when(senhas.gerarHash("Senha@123")).thenReturn("hash-seguro");
        when(usuarios.salvar(any(Usuario.class))).thenAnswer(inv->inv.getArgument(0));
        Map<String,Object> result=service.cadastrar(Map.of("nome","Ana","cpf","12345678900","email","ANA@example.com","senha","Senha@123"));
        assertEquals(1L,result.get("id"));
        verify(usuarios).salvar(argThat(u->u.email().equals("ana@example.com")&&u.senhaHash().equals("hash-seguro")));
    }

    @Test void naoDeveCadastrarEmailDuplicado(){
        when(usuarios.buscarPorEmail("ana@example.com")).thenReturn(Optional.of(usuario()));
        ApplicationException error=assertThrows(ApplicationException.class,()->service.cadastrar(Map.of("nome","Ana","cpf","12345678900","email","ana@example.com","senha","Senha@123")));
        assertEquals(409,error.getStatus());
        verify(usuarios,never()).salvar(any());
    }

    @Test void deveExcluirUsuarioExistente(){
        when(usuarios.buscarPorId(1L)).thenReturn(Optional.of(usuario()));
        when(usuarios.excluir(1L)).thenReturn(true);
        assertDoesNotThrow(() -> service.excluir(1L));
        verify(usuarios).excluir(1L);
    }

    @Test void naoDeveExcluirUsuarioInexistente(){
        when(usuarios.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> service.excluir(99L));
        verify(usuarios, never()).excluir(99L);
    }

    @Test void loginDeveRetornarTokenEUsuarioSemHash(){
        when(usuarios.buscarPorEmail("ana@example.com")).thenReturn(Optional.of(usuario()));
        when(senhas.corresponde("Senha@123","hash-seguro")).thenReturn(true);
        when(tokens.emitir(1L,"ana@example.com")).thenReturn("jwt-token");
        Map<String,Object> result=service.login(Map.of("email","ana@example.com","senha","Senha@123"));
        assertEquals("jwt-token",result.get("accessToken"));
        assertFalse(((Map<?,?>)result.get("usuario")).containsKey("senhaHash"));
    }

    private Usuario usuario(){return new Usuario(1L,"Ana",null,"12345678900",null,null,null,"ana@example.com","hash-seguro",null,null,null,null,null,null,null,null,null,null,null,null,null,Instant.now(),Instant.now());}
}
