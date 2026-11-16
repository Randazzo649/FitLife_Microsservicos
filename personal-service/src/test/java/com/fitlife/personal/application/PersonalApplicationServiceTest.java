package com.fitlife.personal.application;
import com.fitlife.personal.application.port.out.PersonalPersistencePort;
import com.fitlife.personal.application.service.PersonalApplicationService;
import com.fitlife.personal.application.service.PersonalNotFoundException;
import com.fitlife.personal.domain.model.Personal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PersonalApplicationServiceTest {
 @Mock PersonalPersistencePort repository;
 @Test void deveListarPersonais(){
  when(repository.listar("corrida","Curitiba")).thenReturn(List.of(new Personal(1,"Personal Corrida","Corrida","corrida","manhã","Curitiba","PR",null,null)));
  var result=new PersonalApplicationService(repository).listar("corrida","Curitiba");
  assertEquals(1,result.size()); assertEquals("Personal Corrida",result.get(0).get("nome"));
 }
 @Test void naoDeveRegistrarContatoParaPersonalInexistente(){
  when(repository.buscarPorId(9)).thenReturn(Optional.empty());
  assertThrows(PersonalNotFoundException.class,()->new PersonalApplicationService(repository).registrarContato(9,3));
  verify(repository,never()).registrarContato(anyLong(),anyLong());
 }
}
