package org.DesafioJDBC.cliente.model;

import org.DesafioJDBC.produto.model.Cliente;
import org.DesafioJDBC.produto.model.Produto;
import org.DesafioJDBC.produto.view.CriaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ClienteService {
    private Connection connection;

    public ClienteService(Connection connection) {
        this.connection = connection;
    }
    //criar um metodo para criar um novo cliente

    public void insertCliente(Cliente cliente) throws SQLException {
        String sqlCliente = "INSERT INTO Cliente (nome, cpf) VALUES (?, ?)";

        try (PreparedStatement stmtPedido = connection.prepareStatement(sqlCliente)) {
            stmtPedido.setString(1, cliente.getNome());
            stmtPedido.setString(2, cliente.getCpf());
            stmtPedido.executeUpdate();
        }
    }
    public void createClient(Scanner terminal) throws SQLException {
        ClienteService clienteService = new ClienteService(CriaConexao.getConexao());

        System.out.println("--------------------------------");
        System.out.println("Deseja inserir um cliente?");
        System.out.println("Informe o nome desse cliente: ");
        String nome = terminal.nextLine();
        System.out.println("Informe o CPF desse cliente: ");
        String cpf = terminal.nextLine();

        Cliente cliente = Cliente.builder()
                .nome(nome)
                .cpf(cpf)
                .build();

        clienteService.insertCliente(cliente);
        if (cliente != null) {
            System.out.println("Cadastro realizado com sucesso");
        } else {
            System.out.println("Não foi possível realizar o cadastro");
        }
    }
    // getClienteById
    public Cliente getClienteById(int idCliente) throws SQLException {
        // Implementação para buscar o Produto por ID no banco de dados
        Cliente cliente = null;
        String sql = "SELECT * FROM cliente WHERE IdCliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("Nome");
                    String cpf = rs.getString("Cpf");
                    cliente = new Cliente(idCliente, nome,cpf);
                }
            }
        }
        return cliente;
    }
}
