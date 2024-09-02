package org.DesafioJDBC.produto.view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CriaConexao {
    public static Connection getConexao() {
        try {
            final String url = "jdbc:postgresql://localhost:5432/desafio";
            final String usuario = "postgres";
            final String senha = "1234";

            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

