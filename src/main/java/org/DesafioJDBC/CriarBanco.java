package org.DesafioJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CriarBanco {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String driver = "org.postgresql.Driver";
        final String url = "jdbc:postgresql://localhost:5432/";
        final String usuario = "postgres";
        final String senha = "1234";


        Connection conexao = DriverManager.getConnection(url,usuario,senha);

        if (conexao.isValid(1)) System.out.println(conexao);

        Statement stmt = conexao.createStatement();
        try{
            //caso queira dropar o banco é so trocar "create" por "drop"
            stmt.execute("CREATE DATABASE desafio");
            System.out.println("Banco criado com sucesso");
            conexao.close();
        } catch (SQLException e){
            System.out.println(e.getMessage());
            conexao.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

