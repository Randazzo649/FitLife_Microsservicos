package com.fitlife.personal.infrastructure.adapter.out.persistence.sqlserver;

import com.fitlife.personal.application.port.out.PersonalPersistencePort;
import com.fitlife.personal.domain.model.Personal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PersonalSqlServerAdapter implements PersonalPersistencePort {
    private final JdbcTemplate jdbc;
    public PersonalSqlServerAdapter(JdbcTemplate jdbc){this.jdbc=jdbc;}
    @Override public List<Personal> listar(String modalidade,String cidade){
        StringBuilder sql=new StringBuilder("SELECT id,nome,titulo,modalidades,disponibilidade,cidade,estado,email,whatsapp FROM personal WHERE 1=1");
        List<Object> parametros=new ArrayList<>();
        if(modalidade!=null&&!modalidade.isBlank()){sql.append(" AND LOWER(modalidades) LIKE ?");parametros.add("%"+modalidade.toLowerCase()+"%");}
        if(cidade!=null&&!cidade.isBlank()){sql.append(" AND LOWER(cidade) LIKE ?");parametros.add("%"+cidade.toLowerCase()+"%");}
        sql.append(" ORDER BY id");
        return jdbc.query(sql.toString(),(rs,n)->new Personal(rs.getLong("id"),rs.getString("nome"),rs.getString("titulo"),rs.getString("modalidades"),rs.getString("disponibilidade"),rs.getString("cidade"),rs.getString("estado"),rs.getString("email"),rs.getString("whatsapp")),parametros.toArray());
    }
    @Override public Optional<Personal> buscarPorId(long id){
        List<Personal> encontrados=jdbc.query("SELECT id,nome,titulo,modalidades,disponibilidade,cidade,estado,email,whatsapp FROM personal WHERE id=?",
                (rs,n)->new Personal(rs.getLong("id"),rs.getString("nome"),rs.getString("titulo"),rs.getString("modalidades"),rs.getString("disponibilidade"),rs.getString("cidade"),rs.getString("estado"),rs.getString("email"),rs.getString("whatsapp")),id);
        return encontrados.stream().findFirst();
    }
    @Override @Transactional public void registrarContato(long usuarioId,long personalId){
        jdbc.update("IF EXISTS (SELECT 1 FROM contato_personal WHERE usuario_id=? AND personal_id=?) UPDATE contato_personal SET criado_em=SYSUTCDATETIME() WHERE usuario_id=? AND personal_id=? ELSE INSERT INTO contato_personal(usuario_id,personal_id) VALUES(?,?)",
                usuarioId,personalId,usuarioId,personalId,usuarioId,personalId);
    }
}
