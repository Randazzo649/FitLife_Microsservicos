package com.fitlife.treino.infrastructure.adapter.out.persistence.sqlserver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.treino.application.port.out.TreinoPersistencePort;
import com.fitlife.treino.domain.model.Treino;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TreinoSqlServerAdapter implements TreinoPersistencePort {
    private final JdbcTemplate jdbc; private final ObjectMapper mapper;
    public TreinoSqlServerAdapter(JdbcTemplate jdbc,ObjectMapper mapper){this.jdbc=jdbc;this.mapper=mapper;}

    @Override public List<Treino> listarPorUsuario(long usuarioId) {
        return jdbc.query("SELECT id,usuario_id,titulo,cor,categorias_json FROM treino WHERE usuario_id=? ORDER BY id",
                (rs,n)->new Treino(rs.getLong("id"),rs.getLong("usuario_id"),rs.getString("titulo"),rs.getString("cor"),
                        categorias(rs.getString("categorias_json"))),usuarioId);
    }

    @Override public Optional<Treino> buscar(long usuarioId, long treinoId) {
        List<Treino> encontrados = jdbc.query(
                "SELECT id,usuario_id,titulo,cor,categorias_json FROM treino WHERE usuario_id=? AND id=?",
                (rs,n) -> new Treino(rs.getLong("id"), rs.getLong("usuario_id"), rs.getString("titulo"),
                        rs.getString("cor"), categorias(rs.getString("categorias_json"))), usuarioId, treinoId);
        return encontrados.stream().findFirst();
    }

    @Override public boolean excluir(long usuarioId, long treinoId) {
        return jdbc.update("DELETE FROM treino WHERE usuario_id=? AND id=?", usuarioId, treinoId) > 0;
    }

    private List<Map<String,Object>> categorias(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<Map<String,Object>>>() {});
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            throw new IllegalStateException("Categorias armazenadas em formato inválido.", ex);
        }
    }

    @Override public Treino inserir(Treino treino) {
        String sql="INSERT INTO treino(usuario_id,titulo,cor,categorias_json) VALUES(?,?,?,?)";
        KeyHolder keys=new GeneratedKeyHolder();
        final String categoriasJson;
        try { categoriasJson=mapper.writeValueAsString(treino.categorias()); }
        catch (com.fasterxml.jackson.core.JsonProcessingException ex) { throw new IllegalArgumentException("As categorias do treino não puderam ser serializadas.",ex); }
        jdbc.update(connection->{PreparedStatement ps=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1,treino.usuarioId()); ps.setString(2,treino.titulo()); ps.setString(3,treino.cor());
            ps.setString(4,categoriasJson); return ps;},keys);
        Number id=keys.getKey();
        if(id==null) throw new IllegalStateException("O SQL Server não retornou o identificador do treino.");
        return new Treino(id.longValue(),treino.usuarioId(),treino.titulo(),treino.cor(),treino.categorias());
    }

    @Override @Transactional public List<Treino> substituirTodos(long usuarioId,List<Treino> novos) {
        jdbc.update("DELETE FROM treino WHERE usuario_id=?",usuarioId);
        for(Treino treino:novos) inserir(treino);
        return listarPorUsuario(usuarioId);
    }
}
