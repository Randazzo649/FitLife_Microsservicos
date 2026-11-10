package com.fitlife.usuario.adapters.out.persistence.sql;

import com.fitlife.usuario.application.port.out.UsuarioRepositoryPort;
import com.fitlife.usuario.domain.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryJdbc implements UsuarioRepositoryPort {
    private static final String COLUNAS = "id, nome, data_nasc, cpf, genero, telefone, endereco, email, senha";

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT " + COLUNAS + " FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw erroPersistencia("listar usuarios", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(long id) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw erroPersistencia("buscar usuario por id", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE email = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw erroPersistencia("buscar usuario por email", e);
        }
    }

    @Override
    public Usuario inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario(nome, data_nasc, cpf, genero, telefone, endereco, email, senha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, usuario);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getLong(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw erroPersistencia("inserir usuario", e);
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, data_nasc = ?, cpf = ?, genero = ?, " +
                "telefone = ?, endereco = ?, email = ?, senha = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, usuario);
            stmt.setLong(9, usuario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw erroPersistencia("atualizar usuario", e);
        }
    }

    @Override
    public boolean deletarPorId(long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw erroPersistencia("deletar usuario", e);
        }
    }

    private void preencherParametros(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNome());
        stmt.setDate(2, Date.valueOf(usuario.getData_nasc()));
        stmt.setString(3, usuario.getCpf());
        stmt.setString(4, usuario.getGenero());
        stmt.setString(5, usuario.getTelefone());
        stmt.setString(6, usuario.getEndereco());
        stmt.setString(7, usuario.getEmail());
        stmt.setString(8, usuario.getSenha());
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        Date dataNascimento = rs.getDate("data_nasc");
        usuario.setData_nasc(dataNascimento == null ? null : dataNascimento.toLocalDate().toString());
        usuario.setCpf(rs.getString("cpf"));
        usuario.setGenero(rs.getString("genero"));
        usuario.setTelefone(rs.getString("telefone"));
        usuario.setEndereco(rs.getString("endereco"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        return usuario;
    }

    private IllegalStateException erroPersistencia(String operacao, SQLException causa) {
        return new IllegalStateException("Erro ao " + operacao + " no Azure SQL Database.", causa);
    }
}
