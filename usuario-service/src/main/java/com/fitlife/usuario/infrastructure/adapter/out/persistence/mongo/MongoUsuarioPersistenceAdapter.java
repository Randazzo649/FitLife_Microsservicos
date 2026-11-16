package com.fitlife.usuario.infrastructure.adapter.out.persistence.mongo;

import com.fitlife.usuario.application.port.out.UsuarioPersistencePort;
import com.fitlife.usuario.domain.model.Usuario;
import com.fitlife.usuario.domain.exception.UsuarioDuplicadoException;
import org.springframework.dao.DuplicateKeyException;
import java.util.Optional;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class MongoUsuarioPersistenceAdapter implements UsuarioPersistencePort {
    private final MongoTemplate mongo;
    public MongoUsuarioPersistenceAdapter(MongoTemplate mongo) { this.mongo = mongo; }

    @Override
    public long proximoId() {
        CounterDocument counter = mongo.findAndModify(
                Query.query(Criteria.where("_id").is("usuarios")),
                new Update().inc("sequence", 1),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                CounterDocument.class);
        if (counter == null) throw new IllegalStateException("Não foi possível gerar o identificador do usuário.");
        return counter.getSequence();
    }

    @Override
    public Optional<Usuario> buscarPorId(long id) {
        return Optional.ofNullable(mongo.findById(id, UsuarioDocument.class)).map(UsuarioDocument::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        UsuarioDocument d = mongo.findOne(Query.query(Criteria.where("email").is(email.toLowerCase())), UsuarioDocument.class);
        return Optional.ofNullable(d).map(UsuarioDocument::paraDominio);
    }

    @Override
    public boolean existeCpf(String cpf) {
        return mongo.exists(Query.query(Criteria.where("cpf").is(cpf)), UsuarioDocument.class);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        try {
            return mongo.save(UsuarioDocument.de(usuario)).paraDominio();
        } catch (DuplicateKeyException ex) {
            throw new UsuarioDuplicadoException("CPF ou e-mail já cadastrado.", ex);
        }
    }

    @Override
    public boolean excluir(long id) {
        return mongo.remove(Query.query(Criteria.where("_id").is(id)), UsuarioDocument.class)
                .getDeletedCount() > 0;
    }
}
