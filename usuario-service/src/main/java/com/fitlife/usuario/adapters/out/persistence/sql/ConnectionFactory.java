package com.fitlife.usuario.adapters.out.persistence.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        String url = requiredEnvironmentVariable("DB_URL");
        String username = requiredEnvironmentVariable("DB_USERNAME");
        String password = requiredEnvironmentVariable("DB_PASSWORD");
        return DriverManager.getConnection(url, username, password);
    }

    private static String requiredEnvironmentVariable(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("A variavel de ambiente " + name + " nao foi configurada.");
        }
        return value;
    }
}
