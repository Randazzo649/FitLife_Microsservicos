package com.fitlife.personal.application.port.out;
import com.fitlife.personal.domain.model.Personal;
import java.util.List;
import java.util.Optional;
public interface PersonalPersistencePort {
    List<Personal> listar(String modalidade, String cidade);
    Optional<Personal> buscarPorId(long id);
    void registrarContato(long usuarioId, long personalId);
}
