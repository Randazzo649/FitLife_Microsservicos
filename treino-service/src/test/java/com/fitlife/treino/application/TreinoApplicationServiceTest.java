package com.fitlife.treino.application;

import com.fitlife.treino.application.port.out.TreinoEventPublisherPort;
import com.fitlife.treino.application.port.out.TreinoPersistencePort;
import com.fitlife.treino.application.service.TreinoApplicationService;
import com.fitlife.treino.domain.model.Treino;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreinoApplicationServiceTest {
    @Mock TreinoPersistencePort repository;
    @Mock TreinoEventPublisherPort publisher;
    TreinoApplicationService service;
    @BeforeEach void setUp(){service=new TreinoApplicationService(repository,publisher);}

    @Test void deveCriarTreinoEPublicarEvento(){
        Map<String,Object> body=Map.of("titulo","Força","cor","#ff6300","categorias",List.of(Map.of("nome","Peito","exercicios",List.of("Supino"))));
        when(repository.inserir(any(Treino.class))).thenAnswer(inv->new Treino(10L,7L,"Força","#ff6300",((Treino)inv.getArgument(0)).categorias()));
        Map<String,Object> resposta=service.criar(7L,body);
        assertEquals(10L,resposta.get("id"));
        verify(publisher).publicar(argThat(e->e.usuarioId()==7L&&e.tipo().equals("TreinoCriado")));
    }

    @Test void deveExcluirTreinoExistente(){
        when(repository.excluir(7L, 10L)).thenReturn(true);
        assertDoesNotThrow(() -> service.excluir(7L, 10L));
        verify(repository).excluir(7L, 10L);
    }

    @Test void deveRejeitarSessaoSemCategoria(){
        Map<String,Object> body=Map.of("titulo","Força","categorias",List.of());
        assertThrows(IllegalArgumentException.class,()->service.criar(7L,body));
        verifyNoInteractions(repository,publisher);
    }

    @Test void deveSubstituirListaEPublicarEvento(){
        List<Map<String,Object>> body=List.of(Map.of("titulo","Força","categorias",List.of(Map.of("nome","Peito","exercicios",List.of("Supino")))));
        when(repository.substituirTodos(eq(7L),any())).thenAnswer(inv->List.of(new Treino(1L,7L,"Força","#ff6300",List.of(Map.of("nome","Peito","exercicios",List.of("Supino"))))));
        List<Map<String,Object>> result=service.substituir(7L,body);
        assertEquals(1,result.size());
        verify(publisher).publicar(argThat(e->e.usuarioId()==7L&&e.tipo().equals("PlanoTreinoAtualizado")));
    }
}
