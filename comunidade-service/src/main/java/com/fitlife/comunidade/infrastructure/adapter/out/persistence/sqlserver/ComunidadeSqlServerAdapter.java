package com.fitlife.comunidade.infrastructure.adapter.out.persistence.sqlserver;

import com.fitlife.comunidade.application.port.out.ComunidadePersistencePort;
import com.fitlife.comunidade.domain.model.AtividadeTreino;
import com.fitlife.comunidade.domain.model.Evento;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ComunidadeSqlServerAdapter implements ComunidadePersistencePort {
    private final JdbcTemplate jdbc;
    public ComunidadeSqlServerAdapter(JdbcTemplate jdbc){this.jdbc=jdbc;}
    @Override public List<Evento> listar(Long usuarioId){
        String sql="SELECT e.id,e.nome,e.data_evento,e.local,e.descricao,e.imagem,e.criado_por,"+
                "CAST(CASE WHEN p.usuario_id IS NULL THEN 0 ELSE 1 END AS bit) AS inscrito "+
                "FROM evento e LEFT JOIN evento_participante p ON p.evento_id=e.id AND p.usuario_id=? ORDER BY e.data_evento";
        return jdbc.query(sql,(rs,n)->mapEvento(rs.getString("id"),rs.getString("nome"),rs.getString("data_evento"),rs.getString("local"),rs.getString("descricao"),rs.getString("imagem"),numeroLong(rs.getObject("criado_por")),rs.getBoolean("inscrito")),usuarioId==null?-1L:usuarioId);
    }
    @Override public List<Evento> meus(long usuarioId){
        return jdbc.query("SELECT e.id,e.nome,e.data_evento,e.local,e.descricao,e.imagem,e.criado_por,CAST(1 AS bit) AS inscrito FROM evento e JOIN evento_participante p ON p.evento_id=e.id WHERE p.usuario_id=? ORDER BY e.data_evento",
                (rs,n)->mapEvento(rs.getString("id"),rs.getString("nome"),rs.getString("data_evento"),rs.getString("local"),rs.getString("descricao"),rs.getString("imagem"),numeroLong(rs.getObject("criado_por")),rs.getBoolean("inscrito")),usuarioId);
    }
    @Override public Optional<Evento> buscarEvento(String id){
        List<Evento> lista=jdbc.query("SELECT id,nome,data_evento,local,descricao,imagem,criado_por,CAST(0 AS bit) AS inscrito FROM evento WHERE id=?",
                (rs,n)->mapEvento(rs.getString("id"),rs.getString("nome"),rs.getString("data_evento"),rs.getString("local"),rs.getString("descricao"),rs.getString("imagem"),numeroLong(rs.getObject("criado_por")),rs.getBoolean("inscrito")),id);
        return lista.stream().findFirst();
    }
    @Override @Transactional public Evento criar(Evento e,long usuarioId){
        jdbc.update("INSERT INTO evento(id,nome,data_evento,local,descricao,imagem,criado_por) VALUES(?,?,?,?,?,?,?)",e.id(),e.nome(),e.data(),e.local(),e.descricao(),e.imagem(),usuarioId);
        jdbc.update("INSERT INTO evento_participante(evento_id,usuario_id) VALUES(?,?)",e.id(),usuarioId);
        return buscarEvento(e.id()).map(x->new Evento(x.id(),x.nome(),x.data(),x.local(),x.descricao(),x.imagem(),x.criadoPor(),true)).orElseThrow();
    }
    @Override public Evento atualizar(Evento e){
        jdbc.update("UPDATE evento SET nome=?,data_evento=?,local=?,descricao=?,imagem=? WHERE id=?",e.nome(),e.data(),e.local(),e.descricao(),e.imagem(),e.id());
        return new Evento(e.id(), e.nome(), e.data(), e.local(), e.descricao(), e.imagem(), e.criadoPor(), e.inscrito());
    }
    @Override @Transactional public void participar(String eventoId,long usuarioId){
        jdbc.update("IF NOT EXISTS (SELECT 1 FROM evento_participante WHERE evento_id=? AND usuario_id=?) INSERT INTO evento_participante(evento_id,usuario_id) VALUES(?,?)",eventoId,usuarioId,eventoId,usuarioId);
    }
    @Override @Transactional public void salvarAtividade(AtividadeTreino a){
        jdbc.update("IF NOT EXISTS (SELECT 1 FROM atividade_treino WHERE event_id=?) INSERT INTO atividade_treino(event_id,usuario_id,tipo,total_treinos,ocorrido_em) VALUES(?,?,?,?,?)",
                a.eventId(),a.eventId(),a.usuarioId(),a.tipo(),a.totalTreinos(),Timestamp.from(a.ocorridoEm()));
    }
    @Override public List<AtividadeTreino> listarAtividades(Long usuarioId){
        String sql="SELECT event_id,usuario_id,tipo,total_treinos,ocorrido_em FROM atividade_treino";
        if(usuarioId!=null) return jdbc.query(sql+" WHERE usuario_id=? ORDER BY ocorrido_em DESC",(rs,n)->new AtividadeTreino(rs.getString("event_id"),rs.getLong("usuario_id"),rs.getString("tipo"),rs.getInt("total_treinos"),rs.getTimestamp("ocorrido_em").toInstant()),usuarioId);
        return jdbc.query(sql+" ORDER BY ocorrido_em DESC",(rs,n)->new AtividadeTreino(rs.getString("event_id"),rs.getLong("usuario_id"),rs.getString("tipo"),rs.getInt("total_treinos"),rs.getTimestamp("ocorrido_em").toInstant()));
    }
    private Long numeroLong(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }

    private Evento mapEvento(String id,String nome,String data,String local,String descricao,String imagem,Long criadoPor,boolean inscrito){return new Evento(id,nome,data,local,descricao,imagem,criadoPor,inscrito);}
}
