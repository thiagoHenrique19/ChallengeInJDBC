package org.DesafioJDBC.cliente.model;

import org.DesafioJDBC.produto.model.Produto;
import org.DesafioJDBC.produto.view.CriaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProdutoService {

    private Connection connection;

    public ProdutoService(Connection connection) {
        this.connection = connection;
    }

    //criar um metodo para criar um novo produto

    public void insertProduto(Produto produto) throws SQLException {
        String sqlProduto = "INSERT INTO Produto (nome, valor) VALUES (?, ?)";

        try (PreparedStatement stmtPedido = connection.prepareStatement(sqlProduto)) {
            stmtPedido.setString(1, produto.getNome());
            stmtPedido.setFloat(2, produto.getValor());
            stmtPedido.executeUpdate();
        }
    }
    public void receiverProdutcInfo(Scanner terminal) throws SQLException {
        System.out.println("-------------------------------");
        System.out.println("Deseja inserir um produto?");
        System.out.println("Informe o nome do produto: ");
        String nomeProduto = terminal.next();
        System.out.println("Informe o valor: ");
        float valorProduto = terminal.nextFloat();


        Produto produto = Produto
                .builder().
                nome(nomeProduto).
                valor(valorProduto).
                build();

        insertProduto(produto);
        if (produto != null) {
            System.out.println("Cadastro realizado com sucesso");
        } else {
            System.out.println("Não foi possível realizar o cadastro");
        }
    }
    public List<Produto> getAllProdutos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM Produto";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("IdProduto");
                String nome = rs.getString("Nome");
                float valor = rs.getFloat("Valor");
                produtos.add(new Produto(id, nome, valor));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produtos: " + e.getMessage());
        }
        return produtos;
    }
    // Método auxiliar para buscar um Produto pelo ID
    public Produto buscarProdutoPorId(int idProduto) throws SQLException {
        Produto produto = null;
        String sql = "SELECT * FROM Produto WHERE IdProduto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("Nome");
                    double valor = rs.getDouble("Valor");
                    produto = new Produto(idProduto, nome, (float) valor);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto por ID: " + e.getMessage());
        }
        return produto;
    }
}

