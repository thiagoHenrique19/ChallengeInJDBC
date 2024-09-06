package org.DesafioJDBC.cliente.model;

import org.DesafioJDBC.produto.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        ClienteService clienteService = new ClienteService(connection);

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

        public void getAllClientes() throws SQLException {
            List<Cliente> clientes = new ArrayList<>();
            String sql = "SELECT * FROM cliente";

            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("idCliente");
                    String nome = rs.getString("Nome");
                    String cpf = rs.getString("Cpf");
                    clientes.add(new Cliente(id, nome, cpf));
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar clientes: " + e.getMessage());
                throw e;
            }
            if (!clientes.isEmpty()) {
                String bordaSuperior = "╔════════════════════════════════════════════════════════════╗";
                String bordaInferior = "╚════════════════════════════════════════════════════════════╝";
                String divisoria = "╠════════════════════════════════════════════════════════════╣";

                System.out.println(bordaSuperior);
                System.out.println("║                  Clientes Cadastrados                      ║");
                System.out.println(divisoria);

                for (Cliente cliente : clientes) {
                    String cpf = cliente.getCpf().replaceAll("[^0-9]", "").replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
                    System.out.printf("║ ID: %-5d | Nome: %-18s | CPF: %-14s ║%n", cliente.getId(), cliente.getNome(), cpf);
                }

                System.out.println(bordaInferior);
            } else {
                System.out.println("Nenhum cliente encontrado.");
            }
        }
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
