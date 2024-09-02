package org.DesafioJDBC.cliente.model;

import org.DesafioJDBC.produto.model.ItemPedido;
import org.DesafioJDBC.produto.model.Pedido;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PedidoComItensService {
    private Connection connection;
    private PedidoService pedidoService;
    private ItemPedidoService itemPedidoService;

    public PedidoComItensService(Connection connection) throws SQLException {
        this.connection = connection;
        this.pedidoService = new PedidoService(connection);
        this.itemPedidoService = new ItemPedidoService(connection);
    }
    public void criarPedidoComItens(Scanner terminal) throws SQLException {
        System.out.println("Informe o ID do cliente para o pedido:");
        int idCliente = terminal.nextInt();

        Pedido pedido = new Pedido();
        pedido.setId(idCliente);
        pedidoService.createPedido(pedido);

        int idPedido = pedidoService.buscarUltimoIdPedido();

        List<ItemPedido> itens = adicionarItensAoPedido(terminal, idPedido);

        pedido.setItensPedidos(itens);

        exibirPedidoCompleto(idPedido);
    }

    private List<ItemPedido> adicionarItensAoPedido(Scanner terminal, int idPedido) throws SQLException {
        List<ItemPedido> itens = new ArrayList<>();
        boolean adicionarMaisItens = true;

        while (adicionarMaisItens) {
            System.out.println("Informe o ID do produto que deseja adicionar:");
            int idProduto = terminal.nextInt();

            System.out.println("Informe a quantidade do produto:");
            int quantidade = terminal.nextInt();

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setId(idPedido);
            itemPedido.setId(idProduto);
            itemPedido.setQuantidade(quantidade);

            itens.add(itemPedido);
            itemPedidoService.inserirItensPedido(itens);
            System.out.println("Item adicionado com sucesso!");

            System.out.println("Deseja adicionar mais itens? (1 para Sim, 0 para Não):");
            int escolha = terminal.nextInt();
            adicionarMaisItens = escolha == 1;
        }
        return itens;
    }
    public void exibirPedidoCompleto(int idPedido) throws SQLException {
        Pedido pedido = pedidoService.getPedidoPorId(idPedido);

        if (pedido != null) {
            System.out.println("=====================================");
            System.out.printf("ID Pedido: %d%n", pedido.getId());
            System.out.printf("ID Cliente: %d%n", pedido.getId());

            List<ItemPedido> itens = itemPedidoService.getItemPedidoByIdPedido(idPedido);
            System.out.println("Itens do Pedido:");
            for (ItemPedido item : itens) {
                System.out.printf("ID Produto: %d, Quantidade: %d%n",
                        item.getId(), item.getQuantidade());
            }
            System.out.println("=====================================");
        } else {
            System.out.println("Pedido não encontrado.");
        }
    }
}
