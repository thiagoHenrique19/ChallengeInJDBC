package org.DesafioJDBC.cliente.model;

import org.DesafioJDBC.produto.model.ItemPedido;
import org.DesafioJDBC.produto.model.Pedido;
import org.DesafioJDBC.produto.model.Produto;
import org.DesafioJDBC.produto.view.CriaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoService {
    private Connection connection;

    public ItemPedidoService(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void inserirItensPedido(List<ItemPedido> pedidosParaInserir) throws SQLException {
        String sqlItemPedido = "INSERT INTO ItemPedido (IdPedido, IdProduto, Quantidade) VALUES (?, ?, ?)";

        try (PreparedStatement stmtItemPedido = connection.prepareStatement(sqlItemPedido)) {
            for (ItemPedido itemPedido : pedidosParaInserir) {
                stmtItemPedido.setInt(1, itemPedido.getId());
                stmtItemPedido.setInt(2, itemPedido.getProduto().getId());
                stmtItemPedido.setInt(3, itemPedido.getQuantidade());
                stmtItemPedido.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inserir itens do pedido: " + e.getMessage());
            throw e;
        }
    }

    public List<ItemPedido> getItemPedidoById(int idItemPedido) throws SQLException {
        List<ItemPedido> itemPedido = new ArrayList<>();
        String sql = "SELECT * FROM ItemPedido WHERE IdItemPedido = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idItemPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("idItemPedido");
                    int idProduto = rs.getInt("idProduto");
                    int quantidade = rs.getInt("quantidade");

                    ProdutoService produtoService = new ProdutoService(CriaConexao.getConexao());
                    Produto produto = produtoService.buscarProdutoPorId(idProduto);

                    itemPedido.add(ItemPedido
                            .builder()
                            .id(id)
                            .produto(produto)
                            .quantidade(quantidade)
                            .build());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens do pedido: " + e.getMessage());
            throw e;
        }
        return itemPedido;
    }

    public List<ItemPedido> getItemPedidoByIdPedido(int pedidoId) throws SQLException {
        List<ItemPedido> itemPedido = new ArrayList<>();
        String sql = "SELECT * FROM ItemPedido WHERE idpedido = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("idItemPedido");
                    int idProduto = rs.getInt("idProduto");
                    int quantidade = rs.getInt("quantidade");

                    ProdutoService produtoService = new ProdutoService(CriaConexao.getConexao());
                    Produto produto = produtoService.buscarProdutoPorId(idProduto);

                    itemPedido.add( ItemPedido
                            .builder()
                            .id(id)
                            .produto(produto)
                            .quantidade(quantidade)
                            .build());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens do pedido: " + e.getMessage());
            throw e;
        }
        return itemPedido;
    }
}



