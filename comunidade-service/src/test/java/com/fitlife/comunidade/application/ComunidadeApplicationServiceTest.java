package com.fitlife.comunidade.application;
import com.fitlife.comunidade.application.port.out.ComunidadePersistencePort;
import com.fitlife.comunidade.application.service.ComunidadeApplicationService;
import com.fitlife.comunidade.application.service.EventoForbiddenException;
import com.fitlife.comunidade.domain.model.Evento;
import java.util.Optional;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ComunidadeApplicationServiceTest {
 @Mock ComunidadePersistencePort repository;
 @Test void somenteCriadorPodeEditarEvento(){
  when(repository.buscarEvento("abc")).thenReturn(Optional.of(new Evento("abc","Corrida","2026-12-01T08:00","Parque",null,null,11L,false)));
  var service=new ComunidadeApplicationService(repository);
  Map<String,Object> dados=Map.of("nome","Corrida","data","2026-12-02T08:00","local","Parque","usuarioId",12L);
  assertThrows(EventoForbiddenException.class,()->service.atualizar("abc",dados));
  verify(repository,never()).atualizar(any());
 }
 @Test void deveRejeitarEventoSemLocal(){
  var service=new ComunidadeApplicationService(repository);
  Map<String,Object> dados=Map.of("nome","Corrida","data","2026-12-01T08:00","usuarioId",3L);
  assertThrows(IllegalArgumentException.class,()->service.criar(dados));
 }
}
